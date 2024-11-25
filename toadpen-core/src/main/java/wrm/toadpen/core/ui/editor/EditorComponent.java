package wrm.toadpen.core.ui.editor;

import com.jthemedetecor.OsThemeDetector;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import lombok.SneakyThrows;
import org.fife.rsta.ui.CollapsibleSectionPanel;
import org.fife.rsta.ui.search.FindToolBar;
import org.fife.ui.rsyntaxtextarea.FileTypeUtil;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.jspecify.annotations.Nullable;
import wrm.toadpen.core.ui.UiEvent;

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
    this(file, new RSyntaxTextArea(text, 25, 80));
  }

  public EditorComponent() {
    this(null, new RSyntaxTextArea(25, 80));
  }

  private EditorComponent(File file, RSyntaxTextArea textArea) {
    this.file = file;
    this.textArea = textArea;
    this.textArea.setSyntaxEditingStyle(FileTypeUtil.get().guessContentType(file));
    this.textArea.setCodeFoldingEnabled(true);
    this.textArea.setMarkOccurrences(true);

    final OsThemeDetector detector = OsThemeDetector.getDetector();
    boolean isDark = detector.isDark();
    applyDarkLightTheme(isDark);
    detector.registerListener(this::applyDarkLightTheme);

    // fix home/end buttons for osx. todo: make configurable
    int shift = InputEvent.SHIFT_DOWN_MASK;
    this.textArea.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0), DefaultEditorKit.beginLineAction);
    this.textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, shift),
        DefaultEditorKit.selectionBeginLineAction);
    this.textArea.getInputMap()
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0), DefaultEditorKit.endLineAction);
    this.textArea.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_END, shift),
        DefaultEditorKit.selectionEndLineAction);

    // try out multi selection
    MultiCaret c = new MultiCaret();
    c.setBlinkRate(500);
    this.textArea.setCaret(c);


    setupListeners();

    searchListener = new EditorSearchListener(this);
    findToolBar = new FindToolBar(searchListener);


    scrollpane = new RTextScrollPane(textArea);
    csp = new CollapsibleSectionPanel();
    csp.add(scrollpane);


    csp.addBottomComponent(findToolBar);
  }

  @SneakyThrows
  private void applyDarkLightTheme(boolean isDark) {
    InputStream darkThemeData = isDark
        ? EditorComponent.class.getResourceAsStream(
        "/org/fife/ui/rsyntaxtextarea/themes/dark.xml")
        : EditorComponent.class.getResourceAsStream(
        "/org/fife/ui/rsyntaxtextarea/themes/default.xml");

    Theme theme = Theme.load(darkThemeData);
    theme.apply(this.textArea);
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

  public RSyntaxTextArea getTextArea() {
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

  public String getSelectedText() {
    return textArea.getSelectedText();
  }

  public void replaceSelectedText(String newText) {
    textArea.replaceSelection(newText);
  }

  public List<String> getSupportedSyntaxes() {
    return Stream.of(SyntaxConstants.class.getFields())
        .map(f -> {
          try {
            return (String) f.get(null);
          } catch (IllegalAccessException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .toList();
  }

  public void setSyntax(String selectedSyntax) {
    textArea.setSyntaxEditingStyle(selectedSyntax);
  }

  public String getSyntax() {
    return textArea.getSyntaxEditingStyle();
  }
}
