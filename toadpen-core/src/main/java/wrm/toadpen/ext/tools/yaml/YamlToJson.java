package wrm.toadpen.ext.tools.yaml;

import static wrm.toadpen.ext.tools.json.JsonUtil.serializeJson;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.yaml.snakeyaml.Yaml;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.yaml.toJson")
public class YamlToJson implements ToolManager.TextTool {

    @Override
    public void execute(ToolManager.TextContext context) {
        try {
            String yamlString = context.getSelectedText();
            Yaml yaml = new Yaml();
            Object yamlObject = yaml.load(yamlString);
            StringBuffer buf = new StringBuffer();
            serializeJson(yamlObject, buf, 0);
            context.replaceSelectedText(buf.toString());

        } catch (Exception e) {
            context.replaceSelectedText("Error converting YAML to JSON: " + e.getMessage());
        }
    }


}
