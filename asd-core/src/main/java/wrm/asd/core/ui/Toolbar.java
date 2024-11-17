package wrm.asd.core.ui;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.awt.Image;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import wrm.asd.core.cmd.CommandManager;
import wrm.asd.core.cmd.CommandManager.CommandNoArg;
import wrm.asd.core.cmd.EditorCommands;
import wrm.asd.core.cmd.FileCommands;
import wrm.asd.core.key.KeySet;

@Singleton
@RequiredArgsConstructor
public class Toolbar {

  private final List<CommandNoArg> commands;
  private final KeySet keySet;

  private JToolBar toolBar;
  public UiEvent1<String> OnCommandExecuted = new UiEvent1<>();

  public JToolBar getToolbar() {
    return toolBar;
  }

  @PostConstruct
  @SneakyThrows
  JToolBar createToolbar() {
    toolBar = new JToolBar();
    toolBar.setFloatable(false);

    addCommandButton(FileCommands.FILE_NEW);
    addCommandButton(FileCommands.FILE_OPEN);
    addCommandButton(FileCommands.FILE_SAVE);
    toolBar.addSeparator();
    addCommandButton(EditorCommands.EDITOR_SEARCH);

    return toolBar;
  }

  private void addCommandButton(String commandId) throws IOException {
    CommandManager.CommandNoArg command = commands.stream().filter(c -> c.id().equals(commandId)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Command not found: " + commandId));
    Image img = ImageIO.read(getClass().getResource(command.icon()));
    img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
    JButton btnNew = new JButton(new ImageIcon(img, command.description()));

    String toolTip = command.description();
    KeyStroke keyStroke = keySet.getKeystrokeForCommand(command.id());
    if (keyStroke != null) {
      String keyStrokeStr = getKeystrokeDesc(keyStroke);
      toolTip += " [" + keyStrokeStr + "]";
    }
    btnNew.setToolTipText(toolTip);

    btnNew.addActionListener(e -> OnCommandExecuted.fire(command.id()));
    toolBar.add(btnNew);
  }

  private static String getKeystrokeDesc(KeyStroke keyStroke) {
    String desc = keyStroke.toString().replace("pressed ", "");
    desc = desc.replace("ctrl ", "Ctrl+");
    desc = desc.replace("shift ", "Shift+");
    desc = desc.replace("alt ", "Alt+");
    desc = desc.replace("meta ", "Meta+");
    return desc;
  }
}
