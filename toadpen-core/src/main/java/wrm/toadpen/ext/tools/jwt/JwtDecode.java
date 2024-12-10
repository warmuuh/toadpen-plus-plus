package wrm.toadpen.ext.tools.jwt;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.jwt.decode")
public class JwtDecode implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String selectedText = context.getSelectedText();
    String[] split = selectedText.split("\\.");
    if (split.length != 3) {
      return;
    }
    String decoded = new String(java.util.Base64.getDecoder().decode(split[1]));
    context.replaceSelectedText(decoded);
  }

}
