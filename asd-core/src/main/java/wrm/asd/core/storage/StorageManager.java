package wrm.asd.core.storage;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.File;
import java.lang.module.Configuration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import wrm.asd.core.ui.filetree.FileTree;
import wrm.asd.core.util.PlatformUtil;

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
          new PropertyFileStorage(configs.propertiesBuilder().getConfiguration(), () -> {});
      case PROJECT -> {
        File file = new File(fileTree.getRoot(), ".toad/configuration.properties");
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder =
            configs.propertiesBuilder(file);
        yield new PropertyFileStorage(builder.getConfiguration(), builder::save);
      }
      case APPLICATION -> {
        File file = new File(PlatformUtil.fileInApplicationDir("configuration.properties"));
        var builder = configs.propertiesBuilder(file);
        yield new PropertyFileStorage(builder.getConfiguration(), builder::save);
      }
    };
  }


  public enum StorageType {
    SESSION,
    PROJECT,
    APPLICATION
  }
}
