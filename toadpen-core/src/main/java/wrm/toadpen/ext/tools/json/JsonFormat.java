package wrm.toadpen.ext.tools.json;

import com.formdev.flatlaf.json.Json;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.StringReader;
import lombok.SneakyThrows;
import wrm.toadpen.core.tools.ToolManager;


@Singleton
@Named("tools.json.format")
public class JsonFormat implements ToolManager.TextTool {

  @Override
  @SneakyThrows
  public void execute(ToolManager.TextContext context) {
    Object parse = Json.parse(new StringReader(context.getSelectedText()));
    StringBuffer buf = new StringBuffer();
    JsonUtil.serializeJson(parse, buf, 0);
    context.replaceSelectedText(buf.toString());
  }

}