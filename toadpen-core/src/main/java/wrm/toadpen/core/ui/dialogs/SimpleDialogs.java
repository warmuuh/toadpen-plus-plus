package wrm.toadpen.core.ui.dialogs;

import javax.swing.JOptionPane;
import lombok.experimental.UtilityClass;
import wrm.toadpen.core.ui.MainWindow;

@UtilityClass
public class SimpleDialogs {


  public DialogResult confirmation(String title, String message) {
    int result = JOptionPane.showConfirmDialog(MainWindow.getFrame(), message, title,
        JOptionPane.YES_NO_CANCEL_OPTION);
    return switch (result) {
      case JOptionPane.YES_OPTION -> DialogResult.YES;
      case JOptionPane.NO_OPTION -> DialogResult.NO;
      case JOptionPane.CANCEL_OPTION -> DialogResult.CANCEL;
      default -> DialogResult.CANCEL;
    };
  }

  public DialogResult confirmationYesNo(String title, String message) {
    int result = JOptionPane.showConfirmDialog(MainWindow.getFrame(), message, title,
        JOptionPane.YES_NO_OPTION);
    return switch (result) {
      case JOptionPane.YES_OPTION -> DialogResult.YES;
      case JOptionPane.NO_OPTION -> DialogResult.NO;
      default -> DialogResult.CANCEL;
    };
  }

  public String inputDialog(String title, String message, String defaultValue) {
    Object result = JOptionPane.showInputDialog(MainWindow.getFrame(), message, title,
        JOptionPane.QUESTION_MESSAGE, null, null, defaultValue);
    return result != null ? result.toString() : null;
  }


  public enum DialogResult {
    YES, NO, CANCEL
  }

}
