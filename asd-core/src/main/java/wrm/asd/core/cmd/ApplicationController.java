package wrm.asd.core.cmd;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import wrm.asd.core.behavior.StartBehavior;
import wrm.asd.core.ui.MainWindow;
import wrm.asd.core.ui.StatusBar;
import wrm.asd.core.ui.Toolbar;
import wrm.asd.core.ui.editor.EditorFactory;
import wrm.asd.core.ui.filetree.FileTree;
import wrm.asd.core.ui.menu.Menu;

@Singleton
@RequiredArgsConstructor
public class ApplicationController {

  private final Menu menu;
  private final StatusBar statusBar;
  private final Toolbar toolbar;
  private final FileTree fileTree;
  private final MainWindow mainWindow;

  private final StartBehavior startBehavior;

  private final CommandManager commandManager;


  private final EditorFactory editorFactory;

  @PostConstruct
  void init() {
    toolbar.OnCommandExecuted.addListener(commandManager::executeCommandById);
    fileTree.OnFileDoubleClicked.addListener(
        f -> commandManager.executeCommand(new FileCommands.OpenFileCommand(f)));
  }

  public void start() {
    startBehavior.initialize();
    mainWindow.showWindow();
  }


}
