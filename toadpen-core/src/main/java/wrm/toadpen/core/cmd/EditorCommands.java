package wrm.toadpen.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import wrm.toadpen.core.ui.dialogs.SearchBox;
import wrm.toadpen.core.ui.editor.EditorComponent;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.editor.EditorOptions;

@Factory
public class EditorCommands {

  public static final String EDITOR_SEARCH = "editor.search";
  public static final String EDITOR_REPLACE = "editor.replace";
  public static final String EDITOR_CHOOSE_SYNTAX = "editor.chooseSyntax";
  public static final String EDITOR_SHOW_WHITESPACES = "editor.showWhitespaces";


  @Inject
  MainWindow mainWindow;

  @Inject
  EditorOptions editorOptions;

  @Bean
  @Named(EDITOR_SEARCH)
  CommandManager.CommandNoArg triggerSearchCommand() {
    return new CommandManager.CommandNoArg(EDITOR_SEARCH, "Open Search Dialog in Editor",
        "/icons/search.png", this::triggerSearchDialog);
  }

  @Bean
  @Named(EDITOR_REPLACE)
  CommandManager.CommandNoArg triggerReplaceCommand() {
    return new CommandManager.CommandNoArg(EDITOR_REPLACE, "Open Replace Dialog in Editor",
        "/icons/replace.png", this::triggerReplaceDialog);
  }

  @Bean
  @Named(EDITOR_CHOOSE_SYNTAX)
  CommandManager.CommandNoArg triggerChooseSyntax() {
    return new CommandManager.CommandNoArg(EDITOR_CHOOSE_SYNTAX, "Choose Syntax Highlighting",
        "/icons/syntax.png", this::triggerSyntaxSelection);
  }

  @Bean
  @Named(EDITOR_SHOW_WHITESPACES)
  CommandManager.CommandNoArg triggerShowWhitespaces() {
    return new CommandManager.CommandNoArg(EDITOR_SHOW_WHITESPACES, "Show Whitespace Characters",
        "/icons/paragraph.png", this::toggleWhitespaces);
  }



  private void triggerReplaceDialog() {
    EditorComponent activeEditor = mainWindow.getActiveEditor();
    if (activeEditor != null) {
      activeEditor.triggerReplace();
    }
  }

  private void toggleWhitespaces() {
    editorOptions.setShowWhitespaces(!editorOptions.isShowWhitespaces());
  }

  void triggerSearchDialog() {
    EditorComponent activeEditor = mainWindow.getActiveEditor();
    if (activeEditor != null) {
      activeEditor.triggerSearch();
    }
  }

  void triggerSyntaxSelection() {
    EditorComponent activeEditor = mainWindow.getActiveEditor();
    if (activeEditor != null) {
      SearchBox<String> searchBox =
          new SearchBox<>("Select Syntax", activeEditor.getSupportedSyntaxes());
      String selectedSyntax = searchBox.showDialog();
      if (selectedSyntax != null) {
        activeEditor.setSyntax(selectedSyntax);
      }
    }
  }

}
