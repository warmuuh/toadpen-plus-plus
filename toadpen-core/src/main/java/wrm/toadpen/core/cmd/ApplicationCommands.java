package wrm.toadpen.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.term.TerminalComponent;

@Factory
public class ApplicationCommands {

  public static final String APPLICATION_OPEN_TERMINAL = "application.openTerminal";

  @Inject
  MainWindow mainWindow;
  @Inject
  FileTree fileTree;

  @Inject
  TerminalComponent terminalComponent;

  @Bean
  @Named(APPLICATION_OPEN_TERMINAL)
  CommandManager.CommandNoArg triggerOpenTerminalCommand() {
    return new CommandManager.CommandNoArg(APPLICATION_OPEN_TERMINAL,
        "Open Termial in current directory", "/icons/term.png", this::triggerOpenTerminal);
  }


  void triggerOpenTerminal() {
    terminalComponent.stopIfRunning();
    terminalComponent.start(fileTree.getRoot());
    mainWindow.setSouthPanel(terminalComponent.getComponent());
  }

}
