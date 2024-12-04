package wrm.toadpen.core.ui;

import ModernDocking.Dockable;
import ModernDocking.DockingRegion;
import ModernDocking.api.RootDockingPanelAPI;
import ModernDocking.app.Docking;
import ModernDocking.app.RootDockingPanel;
import ModernDocking.event.DockingEvent;
import ModernDocking.ext.ui.DockingUI;
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
import java.util.List;
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
  private JComponent southComponent = null;

  private final KeySetBinding keySetBinding;

  private static JFrame frame;

  private EditorComponent activeEditor;
  public UiEvent1<EditorComponent> OnNewEditorAdded = new UiEvent1<>();
  public UiEvent1<EditorComponent> OnActiveEditorChanged = new UiEvent1<>();
  public UiEvent1<EditorComponent> OnRequestSaveEditor = new UiEvent1<>();
  public UiEvent1<EditorComponent> OnEditorClosed = new UiEvent1<>();
  public UiEvent OnQuitRequest = new UiEvent();
  private JSplitPane verticalSplit;

  {
    //setup theme before any UI components are created
    setupTheme();
  }

  @PostConstruct
  @SneakyThrows
  void init() {
    frame = new JFrame("ToadPen++");
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    frame.addWindowListener(new java.awt.event.WindowAdapter() {
      @Override
      public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        OnQuitRequest.fire();
      }
    });

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
    verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    verticalSplit.setTopComponent(splitPane);
    frame.add(verticalSplit, BorderLayout.CENTER);

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

  public boolean canCloseAllEditors() {
    for (EditorDockingWrapper editor : getAllEditors()) {
      if (editor.vetoClose()) {
        return false;
      }
    }
    return true;
  }

  private RootDockingPanel setupDocking() {
    Docking.initialize(frame);
    DockingUI.initialize();
    Docking.addDockingListener(evt -> {
      Dockable dockable = evt.getDockable();
      if (dockable instanceof EditorDockingWrapper edw) {

        if (evt.getID() == DockingEvent.ID.DOCKED) {
          setActiveEditor(edw.editor);
        }
        boolean hasRootPane = edw.getRootPane() != null;
        if (!hasRootPane && evt.getID() == DockingEvent.ID.UNDOCKED) {
          SwingUtilities.invokeLater(() -> {
            Docking.deregisterDockable(dockable);
            OnEditorClosed.fire(edw.editor);
          });
        }
      }
    });
    RootDockingPanel root = new RootDockingPanel(frame);
    Settings.setAlwaysDisplayTabMode(true);
    Settings.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    return root;
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
    OnNewEditorAdded.fire(editorComponent);
  }

  public List<EditorDockingWrapper> getAllEditors() {
    Window mainWindow = Docking.getMainWindow();
    return Docking.getDockables().stream()
        .filter(d -> d instanceof EditorDockingWrapper)
        .map(d -> (EditorDockingWrapper) d)
        .toList();
  }

  public @Nullable EditorComponent showEditorIfAlreadyOpened(File file) {
    Window mainWindow = Docking.getMainWindow();
    RootDockingPanelAPI root = Docking.getRootPanels().get(mainWindow);
    for (EditorDockingWrapper dockable : getAllEditors()) {
        EditorComponent editor = dockable.editor;
        if (editor.getFile() != null && editor.getFile().equals(file)) {
          Docking.bringToFront(dockable);
          dockable.grabFocus();
          return editor;
        }
    }
    return null;
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


  public boolean isSouthPanelVisible() {
    return verticalSplit.getBottomComponent() != null;
  }

  public void setSouthPanel(JComponent component) {
    SwingUtilities.invokeLater(() -> {
      verticalSplit.setBottomComponent(component);
      verticalSplit.setDividerLocation(0.8);
    });
  }

  public void closeEditor(EditorComponent editor) {
    getAllEditors().stream()
        .filter(edw -> edw.editor == editor)
        .findFirst()
        .ifPresent(Docking::undock);
  }

  public class EditorDockingWrapper extends JComponent implements Dockable {
    private final EditorComponent editor;
    private final String persistentId;
    public EditorDockingWrapper(EditorComponent text) {
      this.editor = text;
      this.persistentId = editor.getFile() != null
          ? editor.getFile().getAbsolutePath()
          : (editor.getFilename() + this.hashCode());
      setLayout(new BorderLayout());
      add(editor.getComponent(), BorderLayout.CENTER);
      Docking.registerDockable(this);
    }

    @Override
    public String getPersistentID() {
      return persistentId;
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
    public boolean vetoClose() {
      return MainWindow.this.vetoClose(editor);
    }
  }
}
