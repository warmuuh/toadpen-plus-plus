package wrm.toadpen.core.ui.editor;

import jakarta.inject.Singleton;
import java.io.File;

@Singleton
public class EditorFactory {

  public EditorComponent createEditor() {
    return new EditorComponent();
  }

  public EditorComponent createEditor(File file, String text) {
    return new EditorComponent(file, text);

  }
}
