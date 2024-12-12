package wrm.toadpen.ai;

import jakarta.inject.Singleton;
import java.util.function.Consumer;
import wrm.toadpen.core.storage.Storage;
import wrm.toadpen.core.storage.StorageManager;

@Singleton
public class AiOptions {
  private final Storage.StorageProperty<String> modelFile;

  public AiOptions(StorageManager storageManager) {
    this.modelFile = storageManager.getStorage(StorageManager.StorageType.APPLICATION).stringProperty("ai.modelfile", "");
  }

  public String getModelFile() {
      return modelFile.get();
  }

  public void setModelFile(String modelFile) {
      this.modelFile.set(modelFile);
  }

    public void onModelFileChanged(Consumer<String> changeEventConsumer) {
        modelFile.addChangeListener(changeEventConsumer);
    }

}
