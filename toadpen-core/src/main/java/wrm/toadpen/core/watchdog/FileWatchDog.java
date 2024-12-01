package wrm.toadpen.core.watchdog;

import io.avaje.inject.Component;
import io.avaje.inject.PreDestroy;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;

@Component
public class FileWatchDog {

  private DirectoryWatchDog directoryWatchDog;

  private Map<File, List<Runnable>> changeListeners = new HashMap<>();
  private Map<File, Long> pausedFiles = new HashMap<>();
  private Map<File, String> fileHashes = new HashMap<>();

  public FileWatchDog() {
    directoryWatchDog = new DirectoryWatchDog(this::onFileChangeEvent);
  }

  public void watch(File file, Runnable onChange) {
    if (file.isDirectory()) {
      throw new IllegalArgumentException("Can only watch files, not directories");
    }
    changeListeners
        .computeIfAbsent(file, f -> new ArrayList<>())
        .add(onChange);
    fileHashes.put(file, FileHasher.hashFile(file));
    restartWatcher();
  }

  public void unwatch(File file) {
    changeListeners.remove(file);
    fileHashes.remove(file);
    restartWatcher();
  }

  @SneakyThrows
  private void restartWatcher() {
    directoryWatchDog.setWatchedDirectory(changeListeners.keySet().stream()
        .map(f -> f.getParentFile())
        .collect(Collectors.toSet()));
  }

  @PreDestroy
  @SneakyThrows
  public void shutdown() {
    directoryWatchDog.terminateJob();
  }

  public void pauseWatch(File file, int pauseInMs) {
    this.pausedFiles.put(file, System.currentTimeMillis() + pauseInMs);
  }


  private void onFileChangeEvent(File file, FileEventType eventType) {
    cleanupPausedFiles();
    if (pausedFiles.containsKey(file)) {
      return;
    }

    String newHash = file.exists() ? FileHasher.hashFile(file) : "";
    if (newHash.equals(fileHashes.get(file))) {
      return;
    }
    fileHashes.put(file, newHash);

    List<Runnable> listeners = changeListeners.get(file);
    if (listeners != null) {
      for (Runnable listener : listeners) {
        listener.run();
      }
    }
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
