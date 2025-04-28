package wrm.toadpen.ext.tools.url;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;
import wrm.toadpen.core.tools.ToolManager.TextTool;

@Singleton
@Named("tools.url.encode")
public class UrlEncode implements TextTool {
  @Override
  public void execute(ToolManager.TextContext context) {
    String selectedText = context.getSelectedText();
    String encoded = java.net.URLEncoder.encode(selectedText, java.nio.charset.StandardCharsets.UTF_8);
    context.replaceSelectedText(encoded);
  }
}
