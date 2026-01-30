package wrm.toadpen.ext.tools.csv;

import com.formdev.flatlaf.json.Json;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.csv.JsonToCsv")
public class JsonToCsv implements ToolManager.TextTool {

  @Override
  @SneakyThrows
  public void execute(ToolManager.TextContext context) {
    Object parsed = Json.parse(new StringReader(context.getSelectedText()));

    if (!(parsed instanceof List list)) {
      return;
    }

    if (list.isEmpty()) {
      return;
    }

    // Collect all unique keys from all objects
    Set<String> headers = new LinkedHashSet<>();
    List<Map<String, Object>> rows = new ArrayList<>();

    for (Object item : list) {
      if (item instanceof Map map) {
        headers.addAll(map.keySet());
        rows.add(map);
      }
    }

    if (headers.isEmpty()) {
      return;
    }

    // Build CSV
    StringBuilder csv = new StringBuilder();

    // Write header
    List<String> headerList = new ArrayList<>(headers);
    for (int i = 0; i < headerList.size(); i++) {
      csv.append(escapeCsv(headerList.get(i)));
      if (i < headerList.size() - 1) {
        csv.append(",");
      }
    }
    csv.append("\n");

    // Write rows
    for (Map<String, Object> row : rows) {
      for (int i = 0; i < headerList.size(); i++) {
        String key = headerList.get(i);
        Object value = row.get(key);
        String valueStr = value != null ? value.toString() : "";
        csv.append(escapeCsv(valueStr));

        if (i < headerList.size() - 1) {
          csv.append(",");
        }
      }
      csv.append("\n");
    }

    context.replaceSelectedText(csv.toString());
  }

  private String escapeCsv(String value) {
    if (value == null) {
      return "";
    }

    // If contains comma, quote, or newline, wrap in quotes
    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
      // Escape quotes by doubling them
      value = value.replace("\"", "\"\"");
      return "\"" + value + "\"";
    }

    return value;
  }

}
