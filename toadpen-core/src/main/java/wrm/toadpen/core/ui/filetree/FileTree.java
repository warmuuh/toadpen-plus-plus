package wrm.toadpen.core.ui.filetree;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JTree;
import wrm.toadpen.core.ui.UiEvent1;

@Singleton
public class FileTree {
 
  private JTree fileTree;
  private FileSystemModel fileSystemModel;
  private ScrollPane scrollPane;

  private File root = new File(System.getProperty("user.home"));
  public UiEvent1<File> OnFileDoubleClicked = new UiEvent1<>();

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

    fileTree.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
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

    scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    scrollPane.add(fileTree);
    scrollPane.setPreferredSize(new Dimension(200, 200));
  }

  public Component getComponent() {
    return scrollPane;
  }

  public File getRoot() {
    return root;
  }

  public void setRoot(File f) {
    if (f == null) {
        return;
    }
    root = f;
    fileTree.setModel(new FileSystemModel(root));
  }
}
 
