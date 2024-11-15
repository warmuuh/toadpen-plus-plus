package wrm.asd.core.ui.filetree;

import java.io.File;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

class FileSystemModel implements TreeModel {
  private TreeFileNode root;

  private Vector listeners = new Vector();

  public FileSystemModel(File rootDirectory) {
    root = new TreeFileNode(rootDirectory.getAbsoluteFile());
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

//  private void fireTreeNodesChanged(TreePath parentPath, int[] indices, Object[] children) {
//    TreeModelEvent event = new TreeModelEvent(this, parentPath, indices, children);
//    Iterator iterator = listeners.iterator();
//    TreeModelListener listener = null;
//    while (iterator.hasNext()) {
//      listener = (TreeModelListener) iterator.next();
//      listener.treeNodesChanged(event);
//    }
//  }

  public void addTreeModelListener(TreeModelListener listener) {
    listeners.add(listener);
  }

  public void removeTreeModelListener(TreeModelListener listener) {
    listeners.remove(listener);
  }

  class TreeFileNode {
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
  }
}
