package wrm.toadpen.core.storage;

import io.avaje.inject.PostConstruct;
import io.avaje.inject.PreDestroy;
import jakarta.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.util.PlatformUtil;

@Singleton
@RequiredArgsConstructor
public class StorageManager {

  private final FileTree fileTree;

  Map<StorageType, Storage> storageMap = new HashMap<>();


  public Storage getStorage(StorageType type) {
    return storageMap.computeIfAbsent(type, this::loadStorage);
  }

  @SneakyThrows
  private Storage loadStorage(StorageType type) {
    Configurations configs = new Configurations();
    return switch (type) {
      case SESSION ->
          new PropertyFileStorage(configs.propertiesBuilder().getConfiguration(), () -> {
          });
      case PROJECT -> {
        File file = new File(fileTree.getRoot(), ".toad/configuration.properties");
        ensureFileExists(file);
        var builder = configs.propertiesBuilder(file);
        yield new PropertyFileStorage(builder.getConfiguration(), builder::save);
      }
      case APPLICATION -> {
        File file = new File(PlatformUtil.fileInApplicationDir("configuration.properties"));
        ensureFileExists(file);
        var builder = configs.propertiesBuilder(file);
        yield new PropertyFileStorage(builder.getConfiguration(), builder::save);
      }
    };
  }

  private static void ensureFileExists(File file) throws IOException {
    if (!file.exists()) {
      if (file.getParentFile() != null) {
        file.getParentFile().mkdirs();
      }
      file.createNewFile();
    }
  }

  @PreDestroy
  public void saveAll() {
    storageMap.values().forEach(Storage::close);
  }


  public enum StorageType {
    SESSION,
    PROJECT,
    APPLICATION
  }
}
