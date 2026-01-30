package wrm.toadpen.ext.tools.stats;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.stats.CountWords")
public class CountWords implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();

    long lines = text.lines().count();
    long words = text.split("\\s+").length;
    long chars = text.length();
    long charsNoSpaces = text.replaceAll("\\s", "").length();

    String stats = String.format(
      "Lines: %d\nWords: %d\nCharacters: %d\nCharacters (no spaces): %d",
      lines, words, chars, charsNoSpaces
    );

    context.replaceSelectedText(stats);
  }

}
