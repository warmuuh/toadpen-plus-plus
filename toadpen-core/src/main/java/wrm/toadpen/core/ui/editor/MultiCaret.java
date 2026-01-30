package wrm.toadpen.core.ui.editor;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import lombok.SneakyThrows;

class MultiCaret extends DefaultCaret implements DocumentListener {
    private SortedSet<SecondaryCaret> additionalDotOffsets = new TreeSet<>(Comparator.comparing(SecondaryCaret::getOffset));

    private boolean syncingSecondaryCarets = false;

    public void addAdditionalDot(Integer additionalDotAbsolutePosition) {
        final Integer caretOffset = additionalDotAbsolutePosition - getDot();
        SecondaryCaret caret = new SecondaryCaret(this);
//        if (caret != null) {
//            caret.removeChangeListener(caretEvent);
//            caret.deinstall(this);
//        }
        caret.install(getComponent());
        caret.setOffset(caretOffset);

        this.additionalDotOffsets.add(caret);
    }

    @Override
    public void install(JTextComponent c) {
        c.getDocument().addDocumentListener(this);
        super.install(c);
    }

    @Override
    public void deinstall(JTextComponent c) {
        c.getDocument().removeDocumentListener(this);
        super.deinstall(c);
    }

    public void removeAllAdditionalDots() {
        this.additionalDotOffsets.forEach(caret -> {
            caret.deinstall(getComponent());
        });
        this.additionalDotOffsets.clear();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        boolean isCtrl = (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() & e.getModifiers()) != 0;
        if (isCtrl) {
            e.consume();
            return;
        }
        super.mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //support mulitple selection
        boolean isCtrl = (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() & e.getModifiers()) != 0;
        if (isCtrl) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                int pos = getComponent().getUI().viewToModel2D(getComponent(), e.getPoint(),
                    new Position.Bias[] {getDotBias()});
                addAdditionalDot(pos);
            }
            return;
        }

        SwingUtilities.invokeLater(() -> removeAllAdditionalDots());
        super.mousePressed(e);

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

    @Override
    public void insertUpdate(DocumentEvent e) {
        boolean isInUndoRedo = !(e instanceof AbstractDocument.DefaultDocumentEvent);

        if (syncingSecondaryCarets) {
            return;
        }
        syncingSecondaryCarets = true;
        try {
            String inserted = e.getDocument().getText(e.getOffset(), e.getLength());
            //TODO maybe do it insync if isUndoRedo because a) no content change and b) lots of changes will be done in sequence
            Runnable  update = () -> {
                int nrCaret = (-1 * (int) additionalDotOffsets.stream().filter(c -> c.getDot() < getDot()).count())-1;
                for (SecondaryCaret caret : additionalDotOffsets) {
                    nrCaret++;
                    if (nrCaret == 0) {
                        nrCaret++;
                    }
                    try {
                        int insertionOffset = nrCaret < 0 ? -e.getLength() : 0;
                        if (!isInUndoRedo) {
                            e.getDocument().insertString(caret.getDot() + insertionOffset, inserted, null);
                        }
                        caret.setOffset(caret.getOffset() + e.getLength() * nrCaret);
                    } catch (BadLocationException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                syncingSecondaryCarets = false;
            };
            if (isInUndoRedo) {
                update.run();
            } else {
                SwingUtilities.invokeLater(update);
            }
        } catch (BadLocationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        boolean isInUndoRedo = !(e instanceof AbstractDocument.DefaultDocumentEvent);

        if (syncingSecondaryCarets) {
            return;
        }
        syncingSecondaryCarets = true;

        Runnable update = () -> {
            int nrCaret = (-1 * (int) additionalDotOffsets.stream().filter(c -> c.getDot() < getDot()).count())-1;
            for (SecondaryCaret caret : additionalDotOffsets) {
                nrCaret++;
                if (nrCaret == 0) {
                    nrCaret++;
                }
                try {
                    if (nrCaret < 0) {
                        if (!isInUndoRedo) {
                            e.getDocument().remove(caret.getDot(), e.getLength());
                        }
                        caret.setOffset(caret.getOffset() - e.getLength() * (nrCaret));
                    } else {
                        if (!isInUndoRedo) {
                            e.getDocument().remove(caret.getDot()-(e.getLength() * nrCaret), e.getLength());
                        }
                        caret.setOffset(caret.getOffset() - e.getLength() * nrCaret);
                    }
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
            syncingSecondaryCarets = false;
        };
        if (isInUndoRedo) {
            update.run();
        } else {
            SwingUtilities.invokeLater(update);
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
//        System.out.println("changedUpdate:" + e.getLength());
    }
}