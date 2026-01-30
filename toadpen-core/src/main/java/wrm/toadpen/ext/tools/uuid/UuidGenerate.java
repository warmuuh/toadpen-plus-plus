package wrm.toadpen.ext.tools.uuid;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.UUID;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.uuid.generate")
public class UuidGenerate implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String uuid = UUID.randomUUID().toString();
    context.replaceSelectedText(uuid);
  }

}
