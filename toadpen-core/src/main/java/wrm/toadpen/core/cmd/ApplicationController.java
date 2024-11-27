package wrm.toadpen.core.cmd;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.core.behavior.ShutdownBehavior;
import wrm.toadpen.core.behavior.StartBehavior;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.StatusBar;
import wrm.toadpen.core.ui.Toolbar;
import wrm.toadpen.core.ui.editor.EditorFactory;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.ui.menu.ApplicationMenu;
import wrm.toadpen.core.ui.os.OsHandler;

@Singleton
@RequiredArgsConstructor
public class ApplicationController {

  private final ApplicationMenu applicationMenu;
  private final StatusBar statusBar;
  private final Toolbar toolbar;
  private final FileTree fileTree;
  private final MainWindow mainWindow;
  private final OsHandler osHandler;

  private final StartBehavior startBehavior;
  private final ShutdownBehavior shutdownBehavior;

  private final CommandManager commandManager;


  private final EditorFactory editorFactory;

  @PostConstruct
  void init() {
    applicationMenu.OnCommandSelected.addListener(commandManager::executeCommand);
    toolbar.OnCommandExecuted.addListener(commandManager::executeCommandById);
    fileTree.OnFileDoubleClicked.addListener(
        f -> commandManager.executeCommand(new FileCommands.OpenFileCommand(f)));

    osHandler.OnOpenFile.addListener(
        f -> commandManager.executeCommand(new FileCommands.OpenFileCommand(f)));
    osHandler.OnQuitRequest.addListener(shutdownBehavior::shutdown);

    mainWindow.OnActiveEditorChanged.addListener(statusBar::onActiveEditorChanged);
    mainWindow.OnRequestSaveEditor.addListener(editor -> commandManager.executeCommand(new FileCommands.SaveEditorFileCommand(editor)));
    mainWindow.OnQuitRequest.addListener(() -> shutdownBehavior.shutdown(null));

    statusBar.OnSyntaxPanelClicked.addListener(() -> commandManager.executeCommandById(EditorCommands.EDITOR_CHOOSE_SYNTAX));
  }

  public void start() {
    startBehavior.initialize();
    mainWindow.showWindow();
  }


}
