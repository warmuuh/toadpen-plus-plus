package wrm.toadpen.core.ui.menu;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.awt.Image;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import wrm.toadpen.core.cmd.ApplicationCommands;
import wrm.toadpen.core.cmd.CommandManager;
import wrm.toadpen.core.cmd.CommandManager.Command;
import wrm.toadpen.core.cmd.EditorCommands;
import wrm.toadpen.core.cmd.FileCommands;
import wrm.toadpen.core.ui.UiEvent1;

@Singleton
@RequiredArgsConstructor
public class ApplicationMenu {

  private final List<CommandManager.CommandNoArg> commands;

  private JMenuBar mb;
  public UiEvent1<Command> OnCommandSelected = new UiEvent1<>();

  public JMenuBar getMenuBar() {
    return mb;
  }

  @PostConstruct
  @SneakyThrows
  JMenuBar createMenuBar() {
    mb = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    mb.add(fileMenu);
    addCommandEntry(FileCommands.FILE_NEW, fileMenu);
    addCommandEntry(FileCommands.FILE_OPEN, fileMenu);
    addCommandEntry(FileCommands.FILE_SAVE, fileMenu);
    addCommandEntry(ApplicationCommands.APPLICATION_QUIT, fileMenu);


    JMenu editMenu = new JMenu("Edit");
    mb.add(editMenu);
    addCommandEntry(EditorCommands.EDITOR_SEARCH, editMenu);
    addCommandEntry(EditorCommands.EDITOR_CHOOSE_SYNTAX, editMenu);

    return mb;
  }

  private void addCommandEntry(String commandId, JMenu parent) throws IOException {
    CommandManager.CommandNoArg command = commands.stream().filter(c -> c.id().equals(commandId)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Command not found: " + commandId));
    ImageIcon imgIcon = null;
    if (command.icon() != null) {
      Image img = ImageIO.read(getClass().getResource(command.icon()));
      img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
      imgIcon = new ImageIcon(img);
    }
    JMenuItem item = new JMenuItem(command.description(), imgIcon);

    item.addActionListener(e -> OnCommandSelected.fire(command));
    parent.add(item);
  }

  public void addCommandEntry(String path, Command cmd) {
    String[] segments = path.split("\\.");
    JMenu menu = findOrCreateTopMenu(mb, getSegmentName(segments[0]));
    for (int i = 1; i < segments.length - 1; i++) {
      menu = findOrCreateSubMenu(menu, getSegmentName(segments[i]));
    }
    JMenuItem item = new JMenuItem(getSegmentName(segments[segments.length - 1]));
    item.addActionListener(e -> OnCommandSelected.fire(cmd));
    menu.add(item);
  }

  private static String getSegmentName(String segment) {
    //transform segment to start with upper case and have a space between words
    return segment.substring(0, 1).toUpperCase() + segment.substring(1).replaceAll("([A-Z])", " $1");
  }

  private JMenu findOrCreateSubMenu(JMenu mb, String segment) {
    for (int i = 0; i < mb.getItemCount(); i++) {
      JMenu menu = (JMenu) mb.getItem(i);
      if (menu.getText().equals(segment)) {
        return menu;
      }
    }
    JMenu menu = new JMenu(segment);
    mb.add(menu);
    return menu;
  }

  private JMenu findOrCreateTopMenu(JMenuBar mb, String segment) {
    for (int i = 0; i < mb.getMenuCount(); i++) {
      JMenu menu = mb.getMenu(i);
      if (menu.getText().equals(segment)) {
        return menu;
      }
    }
    JMenu menu = new JMenu(segment);
    mb.add(menu);
    return menu;
  }


}
