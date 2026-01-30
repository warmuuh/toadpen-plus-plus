package wrm.toadpen.ext.tools.time;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Instant;
import lombok.SneakyThrows;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.time.IsoToTimestamp")
public class IsoToTimestamp implements ToolManager.TextTool {

  @Override
  @SneakyThrows
  public void execute(ToolManager.TextContext context) {
    String isoString = context.getSelectedText().trim();
    Instant instant = Instant.parse(isoString);
    context.replaceSelectedText(String.valueOf(instant.toEpochMilli()));
  }

}
