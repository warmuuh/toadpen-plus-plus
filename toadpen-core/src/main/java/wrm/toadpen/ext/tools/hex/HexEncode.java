package wrm.toadpen.ext.tools.hex;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.hex.encode")
public class HexEncode implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    StringBuilder hex = new StringBuilder();

    for (byte b : text.getBytes()) {
      hex.append(String.format("%02x", b));
    }

    context.replaceSelectedText(hex.toString());
  }

}
