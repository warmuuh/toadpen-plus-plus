package wrm.toadpen.core.ui.filetree;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;
import org.jetbrains.annotations.NotNull;
import wrm.toadpen.core.ui.UiEvent1;

@Singleton
public class FileTree {
 
  private JTree fileTree;
  private FileSystemModel fileSystemModel;
  private JScrollPane scrollPane;

  private File root = new File(System.getProperty("user.home"));
  public UiEvent1<File> OnFileDoubleClicked = new UiEvent1<>();

  public UiEvent1<File> OnFileCreateQuery = new UiEvent1<>();
  public UiEvent1<File> OnDirCreateQuery = new UiEvent1<>();
  public UiEvent1<File> OnFileRenameQuery = new UiEvent1<>();
  public UiEvent1<File[]> OnFileDeleteQuery = new UiEvent1<>();


  @PostConstruct
  public void init() {
    fileSystemModel = new FileSystemModel(root);
    fileTree = new JTree(fileSystemModel);
    fileTree.setEditable(false);
//    fileTree.addTreeSelectionListener(new TreeSelectionListener() {
//      public void valueChanged(TreeSelectionEvent event) {
//        File file = (File) fileTree.getLastSelectedPathComponent();
//      }
//    });

    var popupMenu = new JPopupMenu();
    popupMenu.add(popupAction("New File", OnFileCreateQuery));
    popupMenu.add(popupAction("New Folder", OnDirCreateQuery));
    popupMenu.add(popupAction("Rename File", OnFileRenameQuery));
    popupMenu.add(new AbstractAction("Delete File") {
      @Override
      public void actionPerformed(ActionEvent e) {
        var selectedFiles = new ArrayList<File>();
        for (var path : fileTree.getSelectionPaths()) {
          FileSystemModel.TreeFileNode node = (FileSystemModel.TreeFileNode) path.getLastPathComponent();
          selectedFiles.add(node.getFile());
        }
        OnFileDeleteQuery.fire(selectedFiles.toArray(new File[0]));
      }
    });

    fileTree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {

        if (SwingUtilities.isRightMouseButton(e)) {
          int row = fileTree.getClosestRowForLocation(e.getX(), e.getY());
          if (!fileTree.isRowSelected(row)) {
            fileTree.setSelectionRow(row);
          }
          popupMenu.show(e.getComponent(), e.getX(), e.getY());
          return;
        }

        if (e.getClickCount() == 2) {
          FileSystemModel.TreeFileNode node = (FileSystemModel.TreeFileNode)
              fileTree.getLastSelectedPathComponent();
          if (node == null) return;
          File nodeInfo = node.getFile();
          if (nodeInfo.isFile()) {
            OnFileDoubleClicked.fire(nodeInfo);
          }
        }
      }
    });
    
    scrollPane = new JScrollPane(fileTree);
//    scrollPane.add(fileTree);
//    scrollPane.setPreferredSize(new Dimension(200, 200));
  }

  private @NotNull AbstractAction popupAction(String name, UiEvent1<File> event) {
    return new AbstractAction(name) {
      @Override
      public void actionPerformed(ActionEvent e) {
        FileSystemModel.TreeFileNode node =
            (FileSystemModel.TreeFileNode) fileTree.getLastSelectedPathComponent();
        event.fire(node.getFile());
      }
    };
  }

  public Component getComponent() {
    return scrollPane;
  }

  public File getRoot() {
    return root;
  }

  public void selectFile(File file) {
    Path relativePath = root.toPath().relativize(file.toPath());
    TreePath node = fileSystemModel.findNode(relativePath);
    if (node == null) {
      return;
    }
    fileTree.getSelectionModel().setSelectionPath(node);
  }

  public void setRoot(File f) {
    if (f == null) {
        return;
    }
    root = f;
    fileTree.setModel(new FileSystemModel(root));
  }

  public void refreshTree(File file) {
    fileSystemModel.refreshNodeWithFile(file);
    fileTree.updateUI();
  }
}
 
