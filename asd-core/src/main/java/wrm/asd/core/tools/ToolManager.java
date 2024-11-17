package wrm.asd.core.tools;

import io.avaje.inject.PostConstruct;
import io.avaje.inject.QualifiedMap;
import jakarta.inject.Singleton;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import wrm.asd.core.cmd.CommandManager;
import wrm.asd.core.ui.MainWindow;
import wrm.asd.core.ui.editor.EditorComponent;
import wrm.asd.core.ui.menu.ApplicationMenu;

@Singleton
public class ToolManager {
  private final CommandManager commandManager;
  private final MainWindow mainWindow;
  private final ApplicationMenu applicationMenu;
  private final Map<String, TextTool> registerTools;

  public ToolManager(CommandManager commandManager,
                     MainWindow mainWindow,
                     ApplicationMenu applicationMenu,
                     @QualifiedMap Map<String, TextTool> registerTools) {
    this.commandManager = commandManager;
    this.mainWindow = mainWindow;
    this.applicationMenu = applicationMenu;
    this.registerTools = registerTools;
  }


  @PostConstruct
  public void init() {
    commandManager.registerCommandExecutor(ToolExecutionCommand.class, command -> {
      TextTool tool = registerTools.get(command.getToolId());
      if (tool != null) {
        tool.execute(new EditorTextContext(mainWindow.getActiveEditor()));
      }
    });

    registerTools.forEach((id, tool) -> {
      applicationMenu.addCommandEntry(id, new ToolExecutionCommand(id));
    });
  }

  @Value
  public class ToolExecutionCommand implements CommandManager.Command {
    String toolId;
  }

  @RequiredArgsConstructor
  private class EditorTextContext implements TextContext {
    private final EditorComponent editor;

    @Override
    public String getSelectedText() {
      String selectedText = editor.getSelectedText();
      if (selectedText == null || selectedText.isEmpty()) {
          return editor.getText();
      }
      return selectedText;
    }

    @Override
    public void replaceSelectedText(String newText) {
      editor.replaceSelectedText(newText);
    }

  }


  public interface TextContext {
    /** returns either the selected text or the whole text if no text is selected */
    String getSelectedText();

    /**
     * replaces the selected text with the given text
     * if no text is selected, the text is inserted at the current cursor position
     * @param newText
     */
    void replaceSelectedText(String newText);
  }

  public interface TextTool {
    void execute(TextContext context);
  }
}
