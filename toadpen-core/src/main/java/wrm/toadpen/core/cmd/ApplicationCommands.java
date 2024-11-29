package wrm.toadpen.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.watchdog.FileWatchDog;
import wrm.toadpen.term.TerminalComponent;

@Factory
public class ApplicationCommands {

  public static final String APPLICATION_TOGGLE_TERMINAL = "application.toggleTerminal";

  @Inject
  MainWindow mainWindow;
  @Inject
  FileTree fileTree;

  @Inject
  TerminalComponent terminalComponent;

  @Bean
  @Named(APPLICATION_TOGGLE_TERMINAL)
  CommandManager.CommandNoArg triggerOpenTerminalCommand() {
    return new CommandManager.CommandNoArg(APPLICATION_TOGGLE_TERMINAL,
        "Toggle Terminal in current directory", "/icons/term.png", this::triggerToggleConsole);
  }


  void triggerToggleConsole() {
    if (!mainWindow.isSouthPanelVisible()) {
      if (!terminalComponent.isRunning()) {
        terminalComponent.start(fileTree.getRoot());
      }
      mainWindow.setSouthPanel(terminalComponent.getComponent());
    } else {
      mainWindow.setSouthPanel(null);
    }
  }

}
