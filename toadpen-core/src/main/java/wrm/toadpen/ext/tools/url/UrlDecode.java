package wrm.toadpen.ext.tools.url;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;
import wrm.toadpen.core.tools.ToolManager.TextTool;

@Singleton
@Named("tools.url.decode")
public class UrlDecode implements TextTool {
  @Override
  public void execute(ToolManager.TextContext context) {
    String selectedText = context.getSelectedText();
    String decoded = java.net.URLDecoder.decode(selectedText, java.nio.charset.StandardCharsets.UTF_8);
    context.replaceSelectedText(decoded);
  }
}
