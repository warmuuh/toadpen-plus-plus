package wrm.toadpen.ext.tools.text;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.text.ToUpperCase")
public class ToUpperCase implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();
    context.replaceSelectedText(text.toUpperCase());
  }

}
