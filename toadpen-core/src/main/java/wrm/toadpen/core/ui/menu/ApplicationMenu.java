package wrm.toadpen.core.ui.menu;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import wrm.toadpen.core.cmd.CommandManager.Command;
import wrm.toadpen.core.ui.UiEvent1;

@Singleton
public class ApplicationMenu {

  private JMenuBar mb;
  public UiEvent1<Command> OnCommandSelected = new UiEvent1<>();

  public JMenuBar getMenuBar() {
    return mb;
  }

  @PostConstruct
  JMenuBar createMenuBar() {
    mb = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem quitItem = new JMenuItem("Quit");
    quitItem.addActionListener(e -> System.exit(0));
    fileMenu.add(quitItem);
    mb.add(fileMenu);


    return mb;
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
