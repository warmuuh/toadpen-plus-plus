package wrm.toadpen.core.ui;

import ModernDocking.Dockable;
import ModernDocking.DockingRegion;
import ModernDocking.api.RootDockingPanelAPI;
import ModernDocking.app.Docking;
import ModernDocking.app.RootDockingPanel;
import ModernDocking.event.DockingEvent;
import ModernDocking.ext.ui.DockingUI;
import ModernDocking.internal.DockableWrapper;
import ModernDocking.internal.DockedSplitPanel;
import ModernDocking.internal.DockedTabbedPanel;
import ModernDocking.internal.DockingPanel;
import ModernDocking.settings.Settings;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jthemedetecor.OsThemeDetector;
import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Taskbar;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.IntConsumer;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jspecify.annotations.Nullable;
import wrm.toadpen.core.key.KeySetBinding;
import wrm.toadpen.core.ui.dialogs.SimpleDialogs;
import wrm.toadpen.core.ui.dialogs.SimpleDialogs.DialogResult;
import wrm.toadpen.core.ui.editor.EditorComponent;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.ui.menu.ApplicationMenu;

@Singleton
@RequiredArgsConstructor
public class MainWindow {

  private final ApplicationMenu applicationMenu;
  private final StatusBar statusBar;
  private final Toolbar toolbar;
  private final FileTree fileTree;

  private final KeySetBinding keySetBinding;

  private static JFrame frame;

  private EditorComponent activeEditor;
  public UiEvent1<EditorComponent> OnActiveEditorChanged = new UiEvent1<>();
  public UiEvent1<EditorComponent> OnRequestSaveEditor = new UiEvent1<>();

  {
    //setup theme before any UI components are created
    setupTheme();
  }

  @PostConstruct
  @SneakyThrows
  void init() {
    frame = new JFrame("ToadPen++");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setJMenuBar(applicationMenu.getMenuBar());
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

    keySetBinding.bindKeys(frame);
  }

  private void setupTheme() {
    final OsThemeDetector detector = OsThemeDetector.getDetector();
    FlatLightLaf.setup(detector.isDark() ? new FlatDarkLaf() : new FlatLightLaf());
    detector.registerListener(
        isDark -> {
          FlatLightLaf.setup(isDark ? new FlatDarkLaf() : new FlatLightLaf());
          SwingUtilities.updateComponentTreeUI(frame);
        });
  }

  private RootDockingPanel setupDocking() {
    Docking.initialize(frame);
    DockingUI.initialize();
    Docking.addDockingListener(evt -> {
      Dockable dockable = evt.getDockable();
      if (dockable instanceof EditorDockingWrapper) {

        if (evt.getID() == DockingEvent.ID.DOCKED) {
          setActiveEditor(((EditorDockingWrapper) dockable).editor);
        }
        boolean hasRootPane = ((EditorDockingWrapper) dockable).getRootPane() != null;
        if (!hasRootPane && evt.getID() == DockingEvent.ID.UNDOCKED) {
          SwingUtilities.invokeLater(() -> {
            Docking.deregisterDockable(dockable);
          });
        }
      }
    });
    RootDockingPanel root = new RootDockingPanel(frame) {
      @Override
      public void setPanel(DockingPanel panel) {
        if (panel instanceof DockedTabbedPanel dtp) {
          registerCloseCallback(dtp);
        }
        if (panel instanceof DockedSplitPanel dsp) {
          if (dsp.getLeft() instanceof DockedTabbedPanel left) {
            registerCloseCallback(left);
          }
          if (dsp.getRight() instanceof DockedTabbedPanel right) {
              registerCloseCallback(right);
          }
        }

        super.setPanel(panel);
      }
    };
    Settings.setAlwaysDisplayTabMode(true);
    Settings.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    return root;
  }

  private void registerCloseCallback(DockedTabbedPanel dtp) {
    try {
      Field tabs = dtp.getClass().getDeclaredField("tabs");
      tabs.setAccessible(true);
      Field panelsField = dtp.getClass().getDeclaredField("panels");
      panelsField.setAccessible(true);
      JTabbedPane tabbedPane = (JTabbedPane) tabs.get(dtp);

      tabbedPane.putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) tabIndex -> {
        try {
          List<DockableWrapper> panels = (List<DockableWrapper>) panelsField.get(dtp);
          Dockable dockable = panels.get(tabIndex).getDockable();
          if (dockable instanceof EditorDockingWrapper edw) {
            if (!vetoClose(edw.editor)) {
              Docking.undock(dockable);
            }
          }

        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      });

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void setActiveEditor(EditorComponent newEditor) {
    EditorComponent oldEditor = activeEditor;
    activeEditor = newEditor;
    if (oldEditor != activeEditor) {
      OnActiveEditorChanged.fire(activeEditor);
    }
  }

  public void showWindow() {
    SwingUtilities.invokeLater(() -> {
      frame.setVisible(true);
    });
  }

  public void addNewEditor(EditorComponent editorComponent) {
    Window mainWindow = Docking.getMainWindow();
    EditorDockingWrapper wrapper = new EditorDockingWrapper(editorComponent);
    editorComponent.OnDirtyChange.addListener(() -> Docking.updateTabInfo(wrapper));
    editorComponent.OnFocus.addListener(() -> setActiveEditor(editorComponent));
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

  public static Frame getFrame() {
    return frame;
  }

  private boolean vetoClose(EditorComponent editor) {
    if (!editor.isDirty()) {
      return false;
    }

    DialogResult result = SimpleDialogs.confirmation("Save changes",
        "Do you want to save changes to " + editor.getFilename() + "?");

    if (result == DialogResult.YES) {
      OnRequestSaveEditor.fire(editor);
    }

    return result == DialogResult.CANCEL;
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
      return editor.getFile() != null ? editor.getFile().getAbsolutePath() : editor.getFilename();
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

    @Override
    public boolean isWrappableInScrollpane() {
      return false; // we decide ourselves when to display scroll pane
    }

    @Override
    public boolean getHasMoreOptions() {
      return true;
    }

    @Override
    public void addMoreOptions(JPopupMenu menu) {
      JMenuItem close = new JMenuItem("Close");
      close.addActionListener(e -> System.out.println("Close"));
      menu.add(close);
    }
  }
}
