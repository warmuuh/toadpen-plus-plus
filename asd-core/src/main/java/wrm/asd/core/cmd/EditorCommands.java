package wrm.asd.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import wrm.asd.core.cmd.CommandManager.CommandHandler;
import wrm.asd.core.cmd.CommandManager.CommandHandlerNoArg;
import wrm.asd.core.ui.editor.EditorComponent;
import wrm.asd.core.ui.MainWindow;

@Factory
public class EditorCommands {

  public static final String EDITOR_SEARCH = "editor.search";


  @Inject
  MainWindow mainWindow;

  @Bean @Named(EDITOR_SEARCH)
  CommandHandlerNoArg triggerSearchCommand() {
    return new CommandHandlerNoArg(EDITOR_SEARCH, "Open Search Dialog in Editor", "/icons/search.png", this::triggerSearchDialog);
  }

  void triggerSearchDialog() {
    EditorComponent activeEditor = mainWindow.getActiveEditor();
    if (activeEditor != null) {
      activeEditor.triggerSearch();
    }
  }

}
