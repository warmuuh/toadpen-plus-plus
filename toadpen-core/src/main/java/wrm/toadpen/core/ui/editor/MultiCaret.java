package wrm.toadpen.core.ui.editor;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Position;
import lombok.SneakyThrows;

class MultiCaret extends DefaultCaret {
    private List<DefaultCaret> additionalDotOffsets = new LinkedList<>();

    public void addAdditionalDot(Integer additionalDotAbsolutePosition) {
        DefaultCaret caret = new DefaultCaret();
        final Integer caretOffset = additionalDotAbsolutePosition - getDot();
//        if (caret != null) {
//            caret.removeChangeListener(caretEvent);
//            caret.deinstall(this);
//        }
        caret.install(getComponent());
        caret.setDot(additionalDotAbsolutePosition, getDotBias());
        getComponent().addCaretListener(e -> {
          caret.setDot(e.getMark() + caretOffset);
          if (e.getDot() != e.getMark()) {
            caret.moveDot(e.getDot() + caretOffset);
          }
        });
        getComponent().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (e.getOffset() == getDot()) {
                    SwingUtilities.invokeLater(() -> {
                      try {
                        String inserted = e.getDocument().getText(e.getOffset(), e.getLength());
                        e.getDocument().insertString(caret.getDot(), inserted, null);
                      } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                      }
                    });
                }
            }

            @Override @SneakyThrows
            public void removeUpdate(DocumentEvent e) {
                if (e.getOffset() == getDot()) {
                    SwingUtilities.invokeLater(() -> {
                        try {
                            e.getDocument().remove(caret.getDot(), e.getLength());
                        } catch (BadLocationException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
//        caret.addChangeListener(caretEvent);
//        this.additionalDotOffsets.add(additionalDotAbsolutePosition - this.getDot());
        this.additionalDotOffsets.add(caret);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        //support mulitple selection
        int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (SwingUtilities.isLeftMouseButton(e) && (e.getModifiers() & ctrl) != 0) {
            int pos = getComponent().getUI().viewToModel2D(getComponent(), e.getPoint(),
                new Position.Bias[] {getDotBias()});
            addAdditionalDot(pos);
        } else {
            super.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int ctrl = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (SwingUtilities.isLeftMouseButton(e) && (e.getModifiers() & ctrl) != 0) {
            //do nothing
        } else {
            super.mouseReleased(e);
        }
    }

    public void paint(Graphics g) {
        super.paint(g);

//        try {
//            TextUI mapper = getComponent().getUI();
            for (Caret addDot : additionalDotOffsets) {
                addDot.setVisible(isVisible());
                addDot.paint(g);
//                Rectangle r = mapper.modelToView(getComponent(), getDot() + addDot, getDotBias());
//
//                if(isVisible()) {
//                    g.setColor(getComponent().getCaretColor());
//                    int paintWidth = 1;
//                    r.x -= paintWidth >> 1;
//                    g.fillRect(r.x, r.y, paintWidth, r.height);
//                }
//                else {
//                    getComponent().repaint(r);
//                }
            }
//        } catch (BadLocationException e) {
//            e.printStackTrace();
//        }
    }

}