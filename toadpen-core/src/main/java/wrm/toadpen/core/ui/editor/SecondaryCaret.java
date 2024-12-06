package wrm.toadpen.core.ui.editor;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class SecondaryCaret extends DefaultCaret implements CaretListener, DocumentListener {
  private int offset;
  private Caret mainCaret;


  public SecondaryCaret(Caret mainCaret) {
    this.offset = 0;
    this.mainCaret = mainCaret;
  }

  @Override
  public void install(JTextComponent c) {
    c.addCaretListener(this);
//    c.getDocument().addDocumentListener(this);
    super.install(c);
  }

  @Override
  public void deinstall(JTextComponent c) {
    c.removeCaretListener(this);
//    c.getDocument().removeDocumentListener(this);
    super.deinstall(c);
  }

  @Override
  public void caretUpdate(CaretEvent e) {
    //sync movement with the main caret
    setDot(e.getMark() + offset);
    if (e.getDot() != e.getMark()) {
      moveDot(e.getDot() + offset);
    }
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    if (e.getOffset() == getDot()) {
      SwingUtilities.invokeLater(() -> {
        try {
          String inserted = e.getDocument().getText(e.getOffset(), e.getLength());
          e.getDocument().insertString(getDot(), inserted, null);
        } catch (BadLocationException ex) {
          throw new RuntimeException(ex);
        }
      });
    }
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    if (e.getOffset() == getDot()) {
      SwingUtilities.invokeLater(() -> {
        try {
          e.getDocument().remove(getDot(), e.getLength());
        } catch (BadLocationException ex) {
          throw new RuntimeException(ex);
        }
      });
    }
  }

  @Override
  public void changedUpdate(DocumentEvent e) {

  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int newOffset) {
//    int offsetDiff = newOffset - offset;
    offset = newOffset;
    setDot(mainCaret.getDot() + offset);
  }
}
