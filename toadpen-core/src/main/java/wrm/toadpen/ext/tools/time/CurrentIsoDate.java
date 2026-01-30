package wrm.toadpen.ext.tools.time;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Instant;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.time.CurrentIsoDate")
public class CurrentIsoDate implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String isoDate = Instant.now().toString();
    context.replaceSelectedText(isoDate);
  }

}
