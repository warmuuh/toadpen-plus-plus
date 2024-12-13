package wrm.toadpen.core.ui.editor;

import jakarta.inject.Singleton;
import java.io.File;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

@Singleton
@RequiredArgsConstructor
public class EditorFactory {

  private final List<CompletionProvider> completionProviders;
  private final EditorOptions editorOptions;

  public EditorComponent createEditor() {
    EditorComponent component = new EditorComponent();
    augment(component);
    return component;
  }

  public EditorComponent createEditor(File file, String text) {
    EditorComponent component = new EditorComponent(file, text);
    augment(component);
    return component;
  }

  private void augment(EditorComponent component) {
    for (CompletionProvider provider : completionProviders) {
      AutoCompletion autoCompletion = new AutoCompletion(provider);
      autoCompletion.setShowDescWindow(true);
      autoCompletion.install(component.getTextArea());
    }
    component.subscribeToOptions(editorOptions);
  }
}
