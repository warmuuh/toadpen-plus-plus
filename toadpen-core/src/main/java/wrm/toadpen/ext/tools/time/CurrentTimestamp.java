package wrm.toadpen.ext.tools.time;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Instant;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.time.CurrentTimestamp")
public class CurrentTimestamp implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String timestamp = String.valueOf(Instant.now().toEpochMilli());
    context.replaceSelectedText(timestamp);
  }

}
