package wrm.toadpen.core.os;

import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import org.jspecify.annotations.Nullable;
import wrm.toadpen.core.ui.MainWindow;

public class EmulatingNativeService implements OsNativeService {
  @Override
  public @Nullable File openFileDialog() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(MainWindow.getFrame());
    if (result != JFileChooser.APPROVE_OPTION) {
      return null;
    }
    return fileChooser.getSelectedFile();
  }

  @Override
  public File saveFileDialog() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(MainWindow.getFrame());
    if (result != JFileChooser.APPROVE_OPTION) {
      return null;
    }
    return fileChooser.getSelectedFile();
  }

  @Override
  public void noteNewRecentDocumentURL(File file) {
    // Do nothing
  }

  @Override
  public List<File> getRecentDocuments() {
    return List.of();
  }
}
