package wrm.asd.core.ui.filetree;

import io.avaje.inject.events.Observes;
import jakarta.inject.Singleton;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JTree;
import wrm.asd.core.behavior.ApplicationStartedEvent;
import wrm.asd.core.ui.UiEvent1;

@Singleton
public class FileTree {
 
  private JTree fileTree;
  private FileSystemModel fileSystemModel;
  private ScrollPane scrollPane;

  private File root = new File(System.getProperty("user.home"));
  public UiEvent1<File> OnFileDoubleClicked = new UiEvent1<>();

  public FileTree() {
    fileSystemModel = new FileSystemModel(root);
    fileTree = new JTree(fileSystemModel);
//    fileTree.setMinimumSize(new Dimension(50, 600));
//    fileTree.setPreferredSize(new Dimension(200, 600));
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
    scrollPane.setMinimumSize(new Dimension(100, 600));
    scrollPane.setPreferredSize(new Dimension(200, 600));
  }


  public void onApplicationStarted(@Observes ApplicationStartedEvent event) {
    if (event.initialDirectory() != null) {
      root = event.initialDirectory();
    } else if (event.initialFile() != null) {
      root = event.initialFile().getParentFile();
    }
    fileTree.setModel(new FileSystemModel(root));
  }

  public Component getComponent() {
    return scrollPane;
  }

  public File getRoot() {
    return root;
  }
}
 
