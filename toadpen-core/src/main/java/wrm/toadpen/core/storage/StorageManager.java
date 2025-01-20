package wrm.toadpen.core.storage;

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
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.jetbrains.annotations.NotNull;
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
//        ensureFileExists(file);
        var builder = getConfigurationBuilder(configs, file);
        yield new PropertyFileStorage(builder.getConfiguration(), builder::save);
      }
      case APPLICATION -> {
        File file = new File(PlatformUtil.getApplicationFileDirectory("configuration.properties"));
//        ensureFileExists(file);
        var builder = getConfigurationBuilder(configs, file);
        yield new PropertyFileStorage(builder.getConfiguration(), builder::save);
      }
    };
  }

  private static @NotNull FileBasedConfigurationBuilder<PropertiesConfiguration> getConfigurationBuilder(
      Configurations configs, File file) {
    var builder = configs.propertiesBuilder(file);
    builder.setAutoSave(true);
    FileBasedBuilderParameters params =
        new Parameters().fileBased().setThrowExceptionOnMissing(false);
    builder.configure(params);
    return builder;
  }

  private static void ensureFileExists(File file) throws IOException {
    if (!file.exists()) {
      if (file.getParentFile() != null) {
        file.getParentFile().mkdirs();
      }
      file.createNewFile();
    }
  }

  public enum StorageType {
    SESSION,
    PROJECT,
    APPLICATION
  }
}
