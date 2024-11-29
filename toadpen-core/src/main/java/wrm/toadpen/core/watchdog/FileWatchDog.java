package wrm.toadpen.core.watchdog;

import io.avaje.inject.Component;
import io.avaje.inject.PreDestroy;
import io.methvin.watcher.DirectoryWatcher;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;

@Component
public class FileWatchDog {

  private DirectoryWatcher watcher;
  private Map<File, Runnable> changeListeners = new HashMap<>();
  private Map<File, Long> pausedFiles = new HashMap<>();

  public void watch(File file, Runnable onChange) {
    if (file.isDirectory()) {
      throw new IllegalArgumentException("Can only watch files, not directories");
    }
    changeListeners.put(file, onChange);
    restartWatcher();
  }

  public void unwatch(File file) {
    changeListeners.remove(file);
    restartWatcher();
  }

  @SneakyThrows
  private void restartWatcher() {
    if (watcher != null) {
      watcher.close();
    }
    watcher = DirectoryWatcher.builder()
        .paths(changeListeners.keySet().stream().map(f -> f.getParentFile().toPath()).toList())
        .listener(event -> {
          cleanupPausedFiles();
          File file = event.rootPath().resolve(event.path()).toFile();
          if (pausedFiles.containsKey(file)) {
            return;
          }
          if (changeListeners.containsKey(file)) {
            changeListeners.get(file).run();
          }
        })
        .build();
    watcher.watchAsync();
  }

  @PreDestroy
  @SneakyThrows
  public void shutdown() {
    if (watcher != null) {
      watcher.close();
    }
  }

  public void pauseWatch(File file, int pauseInMs) {
    this.pausedFiles.put(file, System.currentTimeMillis() + pauseInMs);
  }

  public void cleanupPausedFiles() {
    List<File> toRemove = new ArrayList<>();
    for (Map.Entry<File, Long> entry : pausedFiles.entrySet()) {
      if (entry.getValue() < System.currentTimeMillis()) {
        toRemove.add(entry.getKey());
      }
    }
    for (File file : toRemove) {
      pausedFiles.remove(file);
    }
  }

}
