package wrm.toadpen.ext.tools.hash;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.security.MessageDigest;
import lombok.SneakyThrows;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.hash.Sha256Hash")
public class Sha256Hash implements ToolManager.TextTool {

  @Override
  @SneakyThrows
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(text.getBytes());

    StringBuilder hexString = new StringBuilder();
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }

    context.replaceSelectedText(hexString.toString());
  }

}
