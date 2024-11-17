package wrm.asd.ext.tools.base64;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Base64;
import wrm.asd.core.tools.ToolManager.TextContext;
import wrm.asd.core.tools.ToolManager.TextTool;

@Singleton
@Named("tools.base64.decode")
public class Base64Decode implements TextTool {

  @Override
  public void execute(TextContext context) {
    String selectedText = context.getSelectedText();
    String decoded = new String(Base64.getDecoder().decode(selectedText));
    context.replaceSelectedText(decoded);
  }

}
