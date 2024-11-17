package wrm.asd.core.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import org.jspecify.annotations.Nullable;
import wrm.asd.core.ui.MainWindow;
import wrm.asd.core.ui.components.AutocompleteCombobox;

public class SearchBox<T> {

  private final JDialog dialog;
  private final AutocompleteCombobox comboBox;

  private T chosenItem;

  public SearchBox(String placeholder, List<T> items) {
    dialog = new JDialog(MainWindow.getFrame(), "Swing Tester",
        Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setSize(300, 30);
    dialog.setLocationRelativeTo(MainWindow.getFrame());
    Container dialogContainer = dialog.getContentPane();
    dialogContainer.setLayout(new BorderLayout());

    this.comboBox = new AutocompleteCombobox(items);
    AutocompleteCombobox comboBox = this.comboBox;
    comboBox.setCaseSensitive(false);
    comboBox.setStrict(false);
    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowOpened(WindowEvent e) {
        comboBox.showPopup();
      }
    });

//    comboBox.addItemListener(e -> {
//      if (e.getStateChange() == ItemEvent.SELECTED) {
//        chosenItem = (T) e.getItem();
//        dialog.dispatchEvent(new WindowEvent(
//            dialog, WindowEvent.WINDOW_CLOSING
//        ));
//      }
//    });

    dialogContainer.add(comboBox, BorderLayout.CENTER);

    setupEnterKey();
    setupEscapeKey();
  }

  private void setupEnterKey() {
    JRootPane root = dialog.getRootPane();
    root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "searchbox.confirm"
    );
    root.getActionMap().put("searchbox.confirm", new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
            chosenItem = (T) comboBox.getSelectedItem();
            dialog.dispatchEvent(new WindowEvent(
                dialog, WindowEvent.WINDOW_CLOSING
            ));
          }
        }
    );
  }

  private void setupEscapeKey() {
    JRootPane root = dialog.getRootPane();
    root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "searchbox.cancel"
    );
    root.getActionMap().put("searchbox.cancel", new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
            dialog.dispatchEvent(new WindowEvent(
                dialog, WindowEvent.WINDOW_CLOSING
            ));
          }
        }
    );
  }


  public T showDialog() {
    dialog.setVisible(true);
    return chosenItem;
  }

}
