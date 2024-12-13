package wrm.toadpen.core.ui.editor;

import jakarta.inject.Singleton;
import java.util.function.Consumer;
import wrm.toadpen.core.storage.Storage;
import wrm.toadpen.core.storage.StorageManager;

@Singleton
public class EditorOptions {
  private final Storage.StorageProperty<Boolean> showWhitespaces;
  private final Storage.StorageProperty<Integer> tabSize;

  public EditorOptions(StorageManager storageManager) {
    Storage storage = storageManager.getStorage(StorageManager.StorageType.APPLICATION);
    this.showWhitespaces = storage.booleanProperty("editor.showWhitespaces", false);
    this.tabSize = storage.intProperty("editor.tabSize", 2);
  }


  public boolean isShowWhitespaces() {
    return showWhitespaces.get();
  }

  public int getTabSize() {
    return tabSize.get();
  }

  public void setTabSize(int tabSize) {
    this.tabSize.set(tabSize);
  }

  public void setShowWhitespaces(boolean showWhitespaces) {
    this.showWhitespaces.set(showWhitespaces);
  }

    public void onShowWhitespacesChanged(Consumer<Boolean> changeEventConsumer) {
        showWhitespaces.addChangeListener(changeEventConsumer);
    }

    public void onTabSizeChanged(Consumer<Integer> changeEventConsumer) {
        tabSize.addChangeListener(changeEventConsumer);
    }

}
