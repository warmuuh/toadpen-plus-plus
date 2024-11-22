package wrm.asd.core.ui.dialogs;

import javax.swing.JOptionPane;
import lombok.experimental.UtilityClass;
import wrm.asd.core.ui.MainWindow;

@UtilityClass
public class SimpleDialogs {


  public DialogResult confirmation(String title, String message) {
    int result = JOptionPane.showConfirmDialog(MainWindow.getFrame(), message, title, JOptionPane.YES_NO_CANCEL_OPTION);
    return switch (result) {
      case JOptionPane.YES_OPTION -> DialogResult.YES;
      case JOptionPane.NO_OPTION -> DialogResult.NO;
      case JOptionPane.CANCEL_OPTION -> DialogResult.CANCEL;
      default -> DialogResult.CANCEL;
    };

  }


  public enum DialogResult {
    YES, NO, CANCEL
  }

}
