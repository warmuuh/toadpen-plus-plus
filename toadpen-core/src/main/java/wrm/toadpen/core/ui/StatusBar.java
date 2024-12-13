package wrm.toadpen.core.ui;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import lombok.SneakyThrows;
import wrm.toadpen.core.log.Logger;
import wrm.toadpen.core.ui.editor.EditorComponent;

@Singleton
public class StatusBar {

  private JLabel syntaxField;
  private JLabel cursorField;
  private JPanel statusPanel;
  private EditorComponent editor;
  public UiEvent OnSyntaxPanelClicked = new UiEvent();

  private final CaretListener caretListener = new CaretListener() {
    @Override
    @SneakyThrows
    public void caretUpdate(CaretEvent e) {
      if (editor != null) {
        if (e.getMark() == e.getDot()) {
          int lineOfDot = editor.getTextArea().getLineOfOffset(e.getDot());
          int lineStart = editor.getTextArea().getLineStartOffset(lineOfDot);
            int col = e.getDot() - lineStart;
            cursorField.setText("Line: " + (lineOfDot + 1) + ", Column: " + (col + 1));
        } else {
          int chars = Math.abs(e.getDot() - e.getMark());
          int lineDot = editor.getTextArea().getLineOfOffset(e.getDot());
          int lineMark = editor.getTextArea().getLineOfOffset(e.getMark());
          int lines = Math.abs(lineDot - lineMark)+1;
          cursorField.setText("Selected Lines: " + lines + ", Characters: " + chars);
        }
      }
    }
  };
  private JLabel statusLabel;

  public JComponent getStatusBar() {
    return statusPanel;
  }

  @PostConstruct
  void createStatusBar() {
    statusPanel = new JPanel();
    statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
    statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
    statusLabel = new JLabel("Started");
    statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
    statusPanel.add(statusLabel);

    statusPanel.add(Box.createHorizontalGlue());

    statusPanel.add(new JSeparator(SwingConstants.VERTICAL));
    cursorField = new JLabel();
    cursorField.setBorder(new EmptyBorder(0,5,0,5));
    statusPanel.add(cursorField);

    statusPanel.add(new JSeparator(SwingConstants.VERTICAL));
    syntaxField = new JLabel();
    syntaxField.setBorder(new EmptyBorder(0,5,0,5));
    syntaxField.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    syntaxField.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        e.consume();
        OnSyntaxPanelClicked.fire();
      }
    });
    statusPanel.add(syntaxField);
  }

  public void onActiveEditorChanged(EditorComponent editor) {
    if (this.editor != null) {
      this.editor.getTextArea().removeCaretListener(caretListener);
    }

    if (editor == null) {
      syntaxField.setText("");
      cursorField.setText("");
      this.editor = null;
    } else {
      syntaxField.setText(editor.getSyntax());
      this.editor = editor;
      this.editor.getTextArea().addCaretListener(caretListener);
    }
  }


  public void showLog(Logger.LogEvent message) {
    String messageText = message.getMessage();
    if (message.getException() != null) {
      messageText += " - " + message.getException().getMessage();
    }
    statusLabel.setText(messageText);
    statusLabel.repaint();
  }


}
