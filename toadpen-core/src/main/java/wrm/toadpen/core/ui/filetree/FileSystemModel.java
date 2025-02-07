package wrm.toadpen.core.ui.filetree;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import lombok.SneakyThrows;

class FileSystemModel implements TreeModel {
  private TreeFileNode root;

  private Vector listeners = new Vector();

  @SneakyThrows
  public FileSystemModel(File rootDirectory) {
    root = new TreeFileNode(rootDirectory.getCanonicalFile());
  }

  public Object getRoot() {
    return root;
  }

  public Object getChild(Object parent, int index) {
    TreeFileNode directory = (TreeFileNode) parent;
    return directory.getChildren().get(index);
  }

  public int getChildCount(Object parent) {
    TreeFileNode file = (TreeFileNode) parent;
    return file.getChildren().size();
  }

  public boolean isLeaf(Object node) {
    TreeFileNode file = (TreeFileNode) node;
    return !file.isDirectory();
  }

  public int getIndexOfChild(Object parent, Object child) {
    TreeFileNode directory = (TreeFileNode) parent;
    TreeFileNode file = (TreeFileNode) child;
    return directory.getChildren().indexOf(file);
  }

  public void valueForPathChanged(TreePath path, Object value) {
//    File oldFile = (TreeFile) path.getLastPathComponent();
//    String fileParentPath = oldFile.getParent();
//    String newFileName = (String) value;
//    File targetFile = new File(fileParentPath, newFileName);
//    oldFile.renameTo(targetFile);
//    File parent = new File(fileParentPath);
//    int[] changedChildrenIndices = {getIndexOfChild(parent, targetFile)};
//    Object[] changedChildren = {targetFile};
//    fireTreeNodesChanged(path.getParentPath(), changedChildrenIndices, changedChildren);

  }

  private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
    TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
    Iterator iterator = listeners.iterator();
    TreeModelListener listener = null;
    while (iterator.hasNext()) {
      listener = (TreeModelListener) iterator.next();
      listener.treeNodesChanged(event);
    }
  }

  public void addTreeModelListener(TreeModelListener listener) {
    listeners.add(listener);
  }

  public void removeTreeModelListener(TreeModelListener listener) {
    listeners.remove(listener);
  }

  public TreePath findNode(Path relativePath) {
    ArrayList<Object> path = new ArrayList<>();
    path.add(root);
    TreeFileNode current = root;

    for (int i = 0; i < relativePath.getNameCount(); i++) {
      String name = relativePath.getName(i).toString();
      TreeFileNode next = current.getChildren().stream()
          .filter(node -> node.toString().equals(name))
          .findFirst()
          .orElse(null);
      if (next == null) {
        return null;
      }
      path.add(next);
      current = next;
    }
    return new TreePath(path.toArray());
  }

  public TreePath findNextParentNode(Path relativePath) {
    ArrayList<Object> path = new ArrayList<>();
    path.add(root);
    TreeFileNode current = root;

    for (int i = 0; i < relativePath.getNameCount()-1; i++) {
      String name = relativePath.getName(i).toString();
      TreeFileNode next = current.getChildren().stream()
          .filter(node -> node.toString().equals(name))
          .findFirst()
          .orElse(null);
      if (next == null) {
        return new TreePath(path.toArray());
      }
      path.add(next);
      current = next;
    }
    return new TreePath(path.toArray());
  }

  public void refreshNodeWithFile(File file) {
    TreePath parentPath = findNextParentNode(root.file.toPath().relativize(file.toPath()));
    if (parentPath == null) {
      return;
    }
    TreeFileNode nodeToUpdate = (TreeFileNode) parentPath.getLastPathComponent();
    nodeToUpdate.refreshChildren();
    TreePath parentParentPath = findNextParentNode(root.file.toPath().relativize(nodeToUpdate.file.toPath()));

    int[] indices = {getIndexOfChild(parentParentPath.getLastPathComponent(), nodeToUpdate)};
    Object[] children = {nodeToUpdate};
    fireTreeNodesChanged(parentParentPath, indices, children);
  }


  static class TreeFileNode {
    File file;
    List<TreeFileNode> children;

    public TreeFileNode(File file) {
      this.file = file;
    }

    public TreeFileNode(File parent, String child) {
      this.file = new File(parent, child);
    }

    public File getFile() {
      return file;
    }

    public List<TreeFileNode> getChildren() {
      if (children == null) {
        listChildren();
      }
      return children;
    }

    public void refreshChildren() {
      listChildren();
    }

    private void listChildren() {
      children = new LinkedList<>();
      if (file.isDirectory()) {
        String[] list = file.list();
        for (int i = 0; i < list.length; i++) {
          children.add(new TreeFileNode(file, list[i]));
        }
      }
      children.sort((o1, o2) -> Comparator
          .comparing(TreeFileNode::isFile)
          .thenComparing(TreeFileNode::toString)
          .compare(o1, o2));
    }

    public boolean isDirectory() {
      return file.isDirectory();
    }

    public boolean isFile() {
      return file.isFile();
    }

    public String toString() {
      return file.getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TreeFileNode) {
            return file.equals(((TreeFileNode) obj).file);
        }
        return false;
    }
  }
}
