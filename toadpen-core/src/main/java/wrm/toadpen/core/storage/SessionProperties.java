package wrm.toadpen.core.storage;

import jakarta.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;

@Singleton
public class SessionProperties {

  private final Storage.StorageProperty<List<String>> recentFiles;

  public SessionProperties(StorageManager storageManager) {
    this.recentFiles = storageManager.getStorage(StorageManager.StorageType.SESSION).listProperty("recentFiles", new ArrayList<>());
  }

  public List<String> getRecentFiles() {
    return recentFiles.get();
  }

  @SneakyThrows
  public void addRecentFile(File file) {
    ArrayList<String> files = new ArrayList<>(recentFiles.get());
    String canonicalPath = file.getCanonicalPath();
    files.remove(canonicalPath);
    files.addFirst(canonicalPath);
    recentFiles.set(files);
  }
}
