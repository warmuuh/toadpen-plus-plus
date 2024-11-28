package wrm.toadpen.core.os.macos;

import java.awt.FileDialog;
import java.io.File;
import java.util.List;

public class MacOsNativeService implements wrm.toadpen.core.os.OsNativeService {
  @Override
  public File openFileDialog() {
    NativeFileDialog dlg = new NativeFileDialog("Open File", FileDialog.LOAD);
    dlg.setCanChooseDirectories(false);
    dlg.setCanChooseFiles(true);
    dlg.setShowsHiddenFiles(true);

    dlg.setCanCreateDirectories(true);
    dlg.setVisible(true);

    if (dlg.getFile() == null) {
      return null;
    }
    return new File(dlg.getFile());
  }

  @Override
  public File saveFileDialog() {
    NativeFileDialog dlg = new NativeFileDialog("Save File", FileDialog.SAVE);
    dlg.setCanCreateDirectories(true);
    dlg.setShowsHiddenFiles(true);
    dlg.setVisible(true);

    if (dlg.getFile() == null) {
      return null;
    }
    return new File(dlg.getFile());
  }

  @Override
  public void noteNewRecentDocumentURL(File file) {
    NsDocumentController nsDocumentController = new NsDocumentController();
    nsDocumentController.noteNewRecentDocumentURL(file);
  }

  @Override
  public List<File> getRecentDocuments() {
    NsDocumentController nsDocumentController = new NsDocumentController();
    return nsDocumentController.getRecentDocuments();
  }
}
