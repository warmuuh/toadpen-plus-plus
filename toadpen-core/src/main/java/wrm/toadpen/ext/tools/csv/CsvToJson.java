package wrm.toadpen.ext.tools.csv;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.csv.CsvToJson")
public class CsvToJson implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String csv = context.getSelectedText();
    String[] lines = csv.split("\n");

    if (lines.length < 2) {
      return;
    }

    // Parse header
    String[] headers = parseCsvLine(lines[0]);

    // Parse data rows
    List<Map<String, String>> result = new ArrayList<>();
    for (int i = 1; i < lines.length; i++) {
      String line = lines[i].trim();
      if (line.isEmpty()) {
        continue;
      }

      String[] values = parseCsvLine(line);
      Map<String, String> row = new LinkedHashMap<>();

      for (int j = 0; j < headers.length; j++) {
        String value = j < values.length ? values[j] : "";
        row.put(headers[j], value);
      }

      result.add(row);
    }

    // Serialize to JSON
    StringBuilder json = new StringBuilder();
    json.append("[\n");

    for (int i = 0; i < result.size(); i++) {
      json.append("  {\n");
      Map<String, String> row = result.get(i);
      List<String> keys = new ArrayList<>(row.keySet());

      for (int j = 0; j < keys.size(); j++) {
        String key = keys.get(j);
        String value = row.get(key);
        json.append("    \"").append(escapeJson(key)).append("\": \"")
            .append(escapeJson(value)).append("\"");

        if (j < keys.size() - 1) {
          json.append(",");
        }
        json.append("\n");
      }

      json.append("  }");
      if (i < result.size() - 1) {
        json.append(",");
      }
      json.append("\n");
    }

    json.append("]");
    context.replaceSelectedText(json.toString());
  }

  private String[] parseCsvLine(String line) {
    List<String> result = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;

    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);

      if (c == '"') {
        // Check for escaped quote
        if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
          current.append('"');
          i++; // Skip next quote
        } else {
          inQuotes = !inQuotes;
        }
      } else if (c == ',' && !inQuotes) {
        result.add(current.toString().trim());
        current = new StringBuilder();
      } else {
        current.append(c);
      }
    }

    result.add(current.toString().trim());
    return result.toArray(new String[0]);
  }

  private String escapeJson(String value) {
    return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
  }

}
