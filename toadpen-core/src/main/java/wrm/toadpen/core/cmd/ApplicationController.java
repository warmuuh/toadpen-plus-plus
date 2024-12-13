package wrm.toadpen.core.cmd;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.core.behavior.ShutdownBehavior;
import wrm.toadpen.core.behavior.StartBehavior;
import wrm.toadpen.core.log.Logger;
import wrm.toadpen.core.model.ApplicationModel;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.StatusBar;
import wrm.toadpen.core.ui.Toolbar;
import wrm.toadpen.core.ui.editor.EditorFactory;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.ui.menu.ApplicationMenu;
import wrm.toadpen.core.ui.os.OsHandler;
import wrm.toadpen.core.watchdog.FileWatchDog;

@Singleton
@RequiredArgsConstructor
public class ApplicationController {

  private final ApplicationMenu applicationMenu;
  private final StatusBar statusBar;
  private final Toolbar toolbar;
  private final FileTree fileTree;
  private final MainWindow mainWindow;
  private final OsHandler osHandler;
  private final FileWatchDog fileWatchDog;

  private final StartBehavior startBehavior;
  private final ShutdownBehavior shutdownBehavior;

  private final CommandManager commandManager;

  private final ApplicationModel applicationModel;

  private final Logger logger;

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
    osHandler.OnPreferenceRequest.addListener(() -> commandManager.executeCommandById(ApplicationCommands.APPLICATION_SHOW_SETTINGS));

    mainWindow.OnActiveEditorChanged.addListener(statusBar::onActiveEditorChanged);
    mainWindow.OnRequestSaveEditor.addListener(
        editor -> commandManager.executeCommand(new FileCommands.SaveEditorFileCommand(editor)));
    mainWindow.OnQuitRequest.addListener(() -> shutdownBehavior.shutdown(null));

    mainWindow.OnEditorClosed.addListener(editor -> {
      fileWatchDog.unwatch(editor.getFile());
      editor.close();
    });


    logger.OnLogEvent.addListener(statusBar::showLog);
    statusBar.OnSyntaxPanelClicked.addListener(
        () -> commandManager.executeCommandById(EditorCommands.EDITOR_CHOOSE_SYNTAX));


    applicationModel.OnProjectDirectoryChanged.addListener(fileTree::setRoot);

  }

  public void start() {
    //when app is started from e.g. Macos-Dock with a recent file, there is a
    //file to open in the osHandler already before we even had the change to
    //register the OnOpenFile-listener
    if (osHandler.OnOpenFile.hasValueBuffer()) {
      startBehavior.initialize(osHandler.OnOpenFile.fetchValueBuffer());
    } else {
      startBehavior.initialize();
    }
    mainWindow.showWindow();
  }


}
