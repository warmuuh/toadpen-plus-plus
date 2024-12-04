package wrm.toadpen.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.File;
import java.util.List;
import wrm.toadpen.core.model.ApplicationModel;
import wrm.toadpen.core.search.SearchResult;
import wrm.toadpen.core.search.SearchService;
import wrm.toadpen.core.search.ui.SearchResultDialog;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.watchdog.FileWatchDog;
import wrm.toadpen.term.TerminalComponent;

@Factory
public class ApplicationCommands {

  public static final String APPLICATION_TOGGLE_TERMINAL = "application.toggleTerminal";
  public static final String APPLICATION_SEARCH_EVERYWHERE = "application.searchEverywhere";

  @Inject
  MainWindow mainWindow;
  @Inject
  FileTree fileTree;

  @Inject
  SearchService searchService;

  @Inject
  TerminalComponent terminalComponent;
  @Inject
  ApplicationModel model;

  @Inject
  CommandManager commandManager;

  @Bean
  @Named(APPLICATION_TOGGLE_TERMINAL)
  CommandManager.CommandNoArg triggerOpenTerminalCommand() {
    return new CommandManager.CommandNoArg(APPLICATION_TOGGLE_TERMINAL,
        "Toggle Terminal in current directory", "/icons/term.png", this::triggerToggleConsole);
  }

  @Bean
  @Named(APPLICATION_SEARCH_EVERYWHERE)
  CommandManager.CommandNoArg triggerSearchEverywhereCommand() {
      return new CommandManager.CommandNoArg(APPLICATION_SEARCH_EVERYWHERE,
              "Search Everywhere", "/icons/search.png", this::triggerSearchEverywhere);
  }

  private void triggerSearchEverywhere() {
    SearchResult result = new SearchResultDialog("Search everywhere", term -> {
      File projectDirectory = model.getProjectDirectory();
      List<SearchResult> search = searchService.search(projectDirectory, term);
      return search;
    }).showDialog();

    if (result != null) {
      commandManager.executeCommand(new FileCommands.OpenFileCommand(result.getFile(), result.getLine(), result.getColumn()));
    }
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
