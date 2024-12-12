package wrm.toadpen.ai;

import jakarta.inject.Singleton;
import java.awt.Point;
import java.util.List;
import javax.swing.ListCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.Segment;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ParameterChoicesProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;

@Singleton
public class AiCompletionProvider implements CompletionProvider {

  private CompletionProvider parent;
  private Segment seg = new Segment();
  private Segment segPost = new Segment();

  private AiModel aiModel = null;

  @Override
  public void clearParameterizedCompletionParams() {

  }

  @Override
  public String getAlreadyEnteredText(JTextComponent comp) {
    return "";
  }

  @Override
  public List<Completion> getCompletions(JTextComponent comp) {
    if (aiModel == null || !aiModel.isModelLoaded()){
      return List.of();
    }

    String text = enteredText(comp);
    String completion = aiModel.ask(text);
    return List.of(new AiCompletion(this,  completion));
  }

  @Override
  public List<Completion> getCompletionsAt(JTextComponent comp, Point p) {
    return List.of();
  }

  @Override
  public ListCellRenderer<Object> getListCellRenderer() {
    return null;
  }

  @Override
  public ParameterChoicesProvider getParameterChoicesProvider() {
    return null;
  }

  @Override
  public List<ParameterizedCompletion> getParameterizedCompletions(JTextComponent tc) {
    return List.of();
  }

  @Override
  public char getParameterListEnd() {
    return 0;
  }

  @Override
  public String getParameterListSeparator() {
    return "";
  }

  @Override
  public char getParameterListStart() {
    return 0;
  }

  @Override
  public CompletionProvider getParent() {
    return parent;
  }

  @Override
  public boolean isAutoActivateOkay(JTextComponent tc) {
    return false;
  }

  @Override
  public void setListCellRenderer(ListCellRenderer<Object> r) {

  }

  @Override
  public void setParameterizedCompletionParams(char listStart, String separator, char listEnd) {

  }

  @Override
  public void setParent(CompletionProvider parent) {
    this.parent = parent;
  }

  public void setAiModel(AiModel aiModel) {
    this.aiModel = aiModel;
  }

  public String enteredText(JTextComponent comp) {

    Document doc = comp.getDocument();

    int ctxLen = 100;
    int dot = comp.getCaretPosition();
    int start = dot < ctxLen ? 0 : dot - ctxLen;
    int end = dot + ctxLen;
    if (doc.getLength() < end) {
        end = doc.getLength();
    }
    try {
      doc.getText(start,dot-start, seg);
      doc.getText(dot,end-dot, segPost);
    } catch (BadLocationException ble) {
      ble.printStackTrace();
      return "";
    }

    if (start == end){
      return "";
    }

    return seg.toString() + "$$$" + segPost.toString();

  }

}
