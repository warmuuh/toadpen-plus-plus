package wrm.toadpen.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
import wrm.toadpen.core.tools.ToolManager;
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

  @Inject
  ToolManager toolManager;


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
    // Collect all commands
    List<SearchableAction> actions = commandManager.getAllNoArgsCommands().stream()
        .map(SearchableAction::fromCommand)
        .collect(java.util.stream.Collectors.toList());

    // Add all tools
    toolManager.getRegisteredTools().forEach((toolId, tool) -> {
      actions.add(SearchableAction.fromTool(toolId));
    });

    // Pre-cache all icons
    Map<String, ImageIcon> iconCache = new HashMap<>();
    for (SearchableAction action : actions) {
      if (action.icon != null && !iconCache.containsKey(action.icon)) {
        try {
          Image img = ImageIO.read(getClass().getResource(action.icon));
          img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
          iconCache.put(action.icon, new ImageIcon(img));
        } catch (Exception e) {
          // Icon not found, leave it null
        }
      }
    }

    SearchBox<SearchableAction> searchBox =
        new SearchBox<>("Select Command or Tool...", actions);
    searchBox.setItemToStringFn(SearchableAction::displayName);
    searchBox.setIconFn(action -> action.icon != null ? iconCache.get(action.icon) : null);

    SearchableAction selectedAction = searchBox.showDialog();
    if (selectedAction != null) {
      selectedAction.execute(commandManager);
    }
  }

  // Wrapper class for both commands and tools in search dialog
  private static class SearchableAction {
    private final CommandNoArg command;
    private final String toolId;
    private final String displayName;
    private final String icon;

    private SearchableAction(CommandNoArg command, String toolId, String displayName, String icon) {
      this.command = command;
      this.toolId = toolId;
      this.displayName = displayName;
      this.icon = icon;
    }

    static SearchableAction fromCommand(CommandNoArg command) {
      return new SearchableAction(command, null, command.description(), command.icon());
    }

    static SearchableAction fromTool(String toolId) {
      String displayName = formatToolDisplayName(toolId);
      return new SearchableAction(null, toolId, displayName, null);
    }

    String displayName() {
      return displayName;
    }

    void execute(CommandManager commandManager) {
      if (command != null) {
        commandManager.executeCommand(command);
      } else if (toolId != null) {
        commandManager.executeCommand(new ToolManager.ToolExecutionCommand(toolId));
      }
    }

    private static String formatToolDisplayName(String toolId) {
      // Split by dots and get the last segment
      String[] segments = toolId.split("\\.");

      return Arrays.stream(segments)
          .map(segment -> segment.substring(0, 1).toUpperCase() +
              segment.substring(1))
          .collect(Collectors.joining(" > "));
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
