package wrm.toadpen.core.watchdog;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import lombok.SneakyThrows;

public class DirectoryWatchDog {

  private Set<File> watchedDirectories;

  private ExecutorService executor = Executors.newSingleThreadExecutor();
  private WatchService watcher;
  Map<WatchKey, Path> pathMap = new HashMap<>();

  private final BiConsumer<File, FileEventType> fileChangedConsumer;

  public DirectoryWatchDog(BiConsumer<File, FileEventType> fileChangedConsumer) {
    this.fileChangedConsumer = fileChangedConsumer;
  }

  public void setWatchedDirectory(Set<File> directories) {
    watchedDirectories = directories;
    restartJob();
  }

  @SneakyThrows
  private void restartJob() {
    if (watcher != null) {
      terminateJob();
    }
    watcher = FileSystems.getDefault().newWatchService();
    for (File directory : watchedDirectories) {
      WatchKey key =
          directory.toPath().register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
      pathMap.put(key, directory.toPath());
    }
    executor.submit(this::pollLoop);
  }


  public void terminateJob() throws IOException {
    if (watcher != null) {
      watcher.close();
    }
    watcher = null;
  }


  private void handleEvent(File file, WatchEvent.Kind<Path> kind) {
    FileEventType eventType = FileEventType.fromWatchEventKind(kind);
    fileChangedConsumer.accept(file, eventType);
  }

  private void pollLoop() {
    while (watcher != null) {
      try {
        WatchKey key = watcher.take();
        key.pollEvents().forEach(event -> {
          if (event.kind() == OVERFLOW) {
            return;
          }
          WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
          Path filePath = pathMap.get(key).resolve(pathEvent.context());
          handleEvent(filePath.toFile(), pathEvent.kind());
        });
        key.reset();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

}
