package wrm.asd.core.ui;

import ModernDocking.Dockable;
import ModernDocking.DockingRegion;
import ModernDocking.api.RootDockingPanelAPI;
import ModernDocking.app.Docking;
import ModernDocking.app.RootDockingPanel;
import ModernDocking.event.DockingEvent;
import ModernDocking.ext.ui.DockingUI;
import ModernDocking.settings.Settings;
import com.formdev.flatlaf.FlatLightLaf;
import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Taskbar;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jspecify.annotations.Nullable;
import wrm.asd.core.ui.editor.EditorComponent;
import wrm.asd.core.ui.filetree.FileTree;
import wrm.asd.core.ui.menu.Menu;

@Singleton
@RequiredArgsConstructor
public class MainWindow {

  private final Menu menu;
  private final StatusBar statusBar;
  private final Toolbar toolbar;
  private final FileTree fileTree;
  private JFrame frame;

  private EditorComponent activeEditor;

  @PostConstruct @SneakyThrows
  void init() {
    setupTheme();
    frame = new JFrame("ToadPen++");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setJMenuBar(menu.getMenuBar());
    frame.add(toolbar.getToolbar(), BorderLayout.NORTH);
    frame.add(statusBar.getStatusBar(), BorderLayout.SOUTH);

    BufferedImage icon = ImageIO.read(getClass().getResource("/icons/frog.png"));
    frame.setIconImage(icon);
    Taskbar.getTaskbar().setIconImage(icon);

    frame.setSize(800, 600);
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setOneTouchExpandable(true);
//    splitPane.setDividerLocation(100);
    splitPane.setDividerSize(20);
    frame.add(splitPane, BorderLayout.CENTER);

    splitPane.setLeftComponent(fileTree.getComponent());

    RootDockingPanel rootDockingPanel = setupDocking();
    splitPane.setRightComponent(rootDockingPanel);
  }

  private void setupTheme() {
    FlatLightLaf.setup();
  }

  private RootDockingPanel setupDocking() {
    Docking.initialize(frame);
    DockingUI.initialize();
    Docking.addDockingListener(evt -> {
        Dockable dockable = evt.getDockable();
        if (dockable instanceof EditorDockingWrapper) {
          activeEditor = ((EditorDockingWrapper) dockable).editor;
            boolean hasRootPane = ((EditorDockingWrapper) dockable).getRootPane() != null;
            if (!hasRootPane && evt.getID() == DockingEvent.ID.UNDOCKED){
              SwingUtilities.invokeLater(() -> {
                Docking.deregisterDockable(dockable);
              });
            }
        }
    });
    RootDockingPanel root = new RootDockingPanel(frame);
    Settings.setAlwaysDisplayTabMode(true);
    Settings.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    return root;
  }

  public void showWindow() {
    SwingUtilities.invokeLater(() -> {
      frame.setVisible(true);
    });
  }

  public void addNewEditor(EditorComponent editorComponent) {
    Window mainWindow = Docking.getMainWindow();
    EditorDockingWrapper wrapper = new EditorDockingWrapper(editorComponent);
    editorComponent.OnDirtyChange.addListener(()->Docking.updateTabInfo(wrapper));
    editorComponent.OnFocus.addListener(() -> activeEditor = editorComponent);
    editorComponent.grabFocus();

    RootDockingPanelAPI root = Docking.getRootPanels().get(mainWindow);
    root.dock(wrapper, DockingRegion.CENTER, 0);
  }

  public boolean showEditorIfAlreadyOpened(File file) {
    Window mainWindow = Docking.getMainWindow();
    RootDockingPanelAPI root = Docking.getRootPanels().get(mainWindow);
    for (Dockable dockable : Docking.getDockables()) {
      if (dockable instanceof EditorDockingWrapper) {
        EditorComponent editor = ((EditorDockingWrapper) dockable).editor;
        if (editor.getFile() != null && editor.getFile().equals(file)) {
            Docking.bringToFront(dockable);
            ((EditorDockingWrapper) dockable).grabFocus();
            return true;
        }
      }
    }
    return false;
  }

  public @Nullable EditorComponent getActiveEditor() {
    return activeEditor;
  }

  public Component getFrame() {
    return frame;
  }


  static class EditorDockingWrapper extends JComponent implements Dockable {
    private final EditorComponent editor;

    public EditorDockingWrapper(EditorComponent text) {
      this.editor = text;
      setLayout(new BorderLayout());
      add(editor.getComponent(), BorderLayout.CENTER);
      Docking.registerDockable(this);
    }

    @Override
    public String getPersistentID() {
      return editor.getFilename();
    }

    @Override
    public String getTabText() {
      if (editor.isDirty()) {
        return editor.getFilename() + "*";
      }
      return editor.getFilename();
    }

    @Override
    public String getTabTooltip() {
      return editor.getFile() == null ? "Untitled" : editor.getFile().getAbsolutePath();
    }

  }

}
