package wrm.toadpen.ext.tools.base64;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.Base64;
import wrm.toadpen.core.tools.ToolManager.TextContext;
import wrm.toadpen.core.tools.ToolManager.TextTool;

@Singleton
@Named("tools.base64.encode")
public class Base64Encode implements TextTool {

  @Override
  public void execute(TextContext context) {
    String selectedText = context.getSelectedText();
    String encoded = Base64.getEncoder().encodeToString(selectedText.getBytes());
    context.replaceSelectedText(encoded);
  }

}
