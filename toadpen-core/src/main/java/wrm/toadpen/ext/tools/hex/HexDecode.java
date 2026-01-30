package wrm.toadpen.ext.tools.hex;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.hex.decode")
public class HexDecode implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String hex = context.getSelectedText().replaceAll("\\s", "");

    if (hex.length() % 2 != 0) {
      return;
    }

    byte[] bytes = new byte[hex.length() / 2];
    for (int i = 0; i < hex.length(); i += 2) {
      bytes[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
    }

    context.replaceSelectedText(new String(bytes));
  }

}
