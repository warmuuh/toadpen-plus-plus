package wrm.asd.core.ui.editor;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import lombok.RequiredArgsConstructor;
import org.fife.rsta.ui.search.SearchEvent;
import org.fife.rsta.ui.search.SearchListener;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

@RequiredArgsConstructor
public class EditorSearchListener implements SearchListener {
  private final EditorComponent editorComponent;


  @Override
  public void searchEvent(SearchEvent e) {
    RSyntaxTextArea textArea = editorComponent.getTextArea();
    SearchEvent.Type type = e.getType();
    SearchContext context = e.getSearchContext();
    SearchResult result;

    switch (type) {
      default: // Prevent FindBugs warning later
      case MARK_ALL:
        result = SearchEngine.markAll(textArea, context);
        break;
      case FIND:
        result = SearchEngine.find(textArea, context);
        if (!result.wasFound() || result.isWrapped()) {
          UIManager.getLookAndFeel().provideErrorFeedback(textArea);
        }
        break;
      case REPLACE:
        result = SearchEngine.replace(textArea, context);
        if (!result.wasFound() || result.isWrapped()) {
          UIManager.getLookAndFeel().provideErrorFeedback(textArea);
        }
        break;
      case REPLACE_ALL:
        result = SearchEngine.replaceAll(textArea, context);
        JOptionPane.showMessageDialog(null, result.getCount() +
            " occurrences replaced.");
        break;
    }

    String text;
    if (result.wasFound()) {
      text = "Text found; occurrences marked: " + result.getMarkedCount();
    }
    else if (type==SearchEvent.Type.MARK_ALL) {
      if (result.getMarkedCount()>0) {
        text = "Occurrences marked: " + result.getMarkedCount();
      }
      else {
        text = "";
      }
    }
    else {
      text = "Text not found";
    }
//    statusBar.setLabel(text);
  }

  @Override
  public String getSelectedText() {
    RSyntaxTextArea textArea = editorComponent.getTextArea();
    return textArea.getSelectedText();
  }
}
