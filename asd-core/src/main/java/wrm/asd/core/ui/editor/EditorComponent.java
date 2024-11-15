package wrm.asd.core.ui.editor;

import java.io.File;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.search.FindToolBar;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jspecify.annotations.Nullable;
import wrm.asd.core.ui.UiEvent;

public class EditorComponent {

  public final UiEvent OnFocus = new UiEvent();
  public final UiEvent OnDirtyChange = new UiEvent();

  private CollapsibleSectionPanel csp;
  private RSyntaxTextArea textArea;
  private RTextScrollPane scrollpane;
  private FindToolBar findToolBar;
  private EditorSearchListener searchListener;


  private @Nullable File file;

  private boolean dirtyState = false;

  public EditorComponent(@Nullable File file, String text) {
    this(file, new RSyntaxTextArea(text,25, 80));
  }

  public EditorComponent() {
   this(null, new RSyntaxTextArea(25, 80));
  }

  private EditorComponent(File file,RSyntaxTextArea textArea) {
    this.file = file;
    this.textArea = textArea;
    this.textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
    this.textArea.setCodeFoldingEnabled(true);
    this.textArea.setMarkOccurrences(true);

    setupListeners();

    searchListener = new EditorSearchListener(this);
    findToolBar = new FindToolBar(searchListener);


    scrollpane = new RTextScrollPane(textArea);
    csp = new CollapsibleSectionPanel();
    csp.add(scrollpane);
    csp.addBottomComponent(findToolBar);
  }

  private void setupListeners() {
    this.textArea.addFocusListener(new java.awt.event.FocusAdapter() {
      @Override
      public void focusGained(java.awt.event.FocusEvent evt) {
        OnFocus.fire();
      }
    });
    this.textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        setDirtyState(true);
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        setDirtyState(true);
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        setDirtyState(true);
      }
    });
  }

  public String getFilename() {
    return file == null ? "Untitled" : file.getName();
  }

  public @Nullable File getFile() {
    return file;
  }

  public JComponent getComponent() {
    return csp;
  }

  RSyntaxTextArea getTextArea() {
    return textArea;
  }

  public void close() {
    OnFocus.clear();
  }

  public String getText() {
    return textArea.getText();
  }

  public void setDirtyState(boolean dirtyState) {
    if (this.dirtyState != dirtyState) {
      this.dirtyState = dirtyState;
      OnDirtyChange.fire();
    }
  }

  public boolean isDirty() {
      return dirtyState;
  }

  public void triggerSearch() {
    csp.showBottomComponent(findToolBar);
  }

  public void grabFocus() {
    SwingUtilities.invokeLater(() -> textArea.grabFocus());
  }
}
