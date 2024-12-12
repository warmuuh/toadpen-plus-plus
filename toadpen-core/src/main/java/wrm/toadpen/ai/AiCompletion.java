package wrm.toadpen.ai;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.CompletionProvider;

public class AiCompletion extends AbstractCompletion {

  private final String summary;

  protected AiCompletion(CompletionProvider provider, String summary) {
    super(provider);
    this.summary = summary;
  }

  @Override
  public String getReplacementText() {
    return summary;
  }

  @Override
  public String getSummary() {
    return summary;
  }

  @Override
  public String toString() {
    return getSummary().length() > 10 ? getSummary().substring(0,10) : getSummary();
  }
}
