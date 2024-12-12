package wrm.toadpen.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.awt.Image;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import wrm.toadpen.core.cmd.CommandManager.CommandNoArg;
import wrm.toadpen.core.model.ApplicationModel;
import wrm.toadpen.core.search.SearchResult;
import wrm.toadpen.core.search.SearchService;
import wrm.toadpen.core.search.ui.SearchResultDialog;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.dialogs.SearchBox;
import wrm.toadpen.core.ui.editor.EditorFactory;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.ui.options.OptionsDialog;
import wrm.toadpen.term.TerminalComponent;

@Factory
public class ApplicationCommands {

  public static final String APPLICATION_TOGGLE_TERMINAL = "application.toggleTerminal";
  public static final String APPLICATION_SEARCH_EVERYWHERE = "application.searchEverywhere";
  public static final String APPLICATION_SEARCH_COMMAND = "application.searchCommand";
  public static final String APPLICATION_SHOW_SETTINGS = "application.showSettings";
  public static final String APPLICATION_QUIT = "application.quit";

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

  @Inject
  EditorFactory editorFactory;

  @Inject
  OptionsDialog optionsDialog;


  @Bean
  @Named(APPLICATION_TOGGLE_TERMINAL)
  CommandNoArg triggerOpenTerminalCommand() {
    return new CommandNoArg(APPLICATION_TOGGLE_TERMINAL,
        "Toggle Terminal in current directory", "/icons/term.png", this::triggerToggleConsole);
  }

  @Bean
  @Named(APPLICATION_SEARCH_EVERYWHERE)
  CommandNoArg triggerSearchEverywhereCommand() {
    return new CommandNoArg(APPLICATION_SEARCH_EVERYWHERE,
        "Search Everywhere", "/icons/search.png", this::triggerSearchEverywhere);
  }


  @Bean
  @Named(APPLICATION_SEARCH_COMMAND)
  CommandNoArg triggerSearchCommandCommand() {
    return new CommandNoArg(APPLICATION_SEARCH_COMMAND,
        "Search Command", "/icons/cog-search.png", this::triggerSearchCommand);
  }


  @Bean
  @Named(APPLICATION_QUIT)
  CommandNoArg quitApplicationCommand() {
    return new CommandNoArg(APPLICATION_QUIT,
        "Quit", null, this::quitApplication);
  }

  @Bean
  @Named(APPLICATION_SHOW_SETTINGS)
  CommandNoArg showSettingsCommand() {
    return new CommandNoArg(APPLICATION_SHOW_SETTINGS,
        "Settings", null, this::showSettings);
  }

  private void showSettings() {
    optionsDialog.show();
  }


  private void triggerSearchEverywhere() {
    SearchResult result = new SearchResultDialog("Search everywhere", term -> {
      File projectDirectory = model.getProjectDirectory();
      List<SearchResult> search = searchService.search(projectDirectory, term);
      return search;
    }, editorFactory).showDialog();

    if (result != null) {
      commandManager.executeCommand(
          new FileCommands.OpenFileCommand(result.getFile(), result.getLine(), result.getColumn()));
    }
  }

  private void triggerSearchCommand() {
    SearchBox<CommandNoArg> searchBox =
        new SearchBox<>("Select Command...", commandManager.getAllNoArgsCommands());
    searchBox.setItemToStringFn(CommandNoArg::description);
    searchBox.setRenderItemFn((parent, command) -> {
      ImageIcon icon = null;
      if (command.icon() != null) {
        try {
          Image img = ImageIO.read(getClass().getResource(command.icon()));
          img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
          icon = new ImageIcon(img);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      parent.add(new JLabel(command.description(), icon, JLabel.LEADING));
    });
    CommandNoArg selectedCommand = searchBox.showDialog();
    if (selectedCommand != null) {
      commandManager.executeCommand(selectedCommand);
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


  private void quitApplication() {
    System.exit(0);
  }

}
