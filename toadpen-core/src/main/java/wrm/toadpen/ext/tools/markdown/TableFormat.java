package wrm.toadpen.ext.tools.markdown;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.markdown.TableFormat")
public class TableFormat implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String text = context.getSelectedText();

    // Parse the input into rows and columns
    List<List<String>> rows = parseTable(text);

    if (rows.isEmpty()) {
      return;
    }

    // Calculate column widths
    int numColumns = rows.stream().mapToInt(List::size).max().orElse(0);
    int[] columnWidths = new int[numColumns];

    for (List<String> row : rows) {
      for (int i = 0; i < row.size(); i++) {
        columnWidths[i] = Math.max(columnWidths[i], row.get(i).length());
      }
    }

    // Format as Markdown table
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < rows.size(); i++) {
      List<String> row = rows.get(i);
      result.append("| ");

      for (int j = 0; j < numColumns; j++) {
        String cell = j < row.size() ? row.get(j) : "";
        result.append(padRight(cell, columnWidths[j]));
        result.append(" | ");
      }

      result.append("\n");

      // Add separator after first row (header)
      if (i == 0) {
        result.append("|");
        for (int j = 0; j < numColumns; j++) {
          result.append("-").append("-".repeat(columnWidths[j])).append("-|");
        }
        result.append("\n");
      }
    }

    context.replaceSelectedText(result.toString().trim());
  }

  private List<List<String>> parseTable(String text) {
    List<List<String>> rows = new ArrayList<>();
    String[] lines = text.split("\n");

    // Detect delimiter (pipe, tab, comma, or spaces)
    String delimiter = detectDelimiter(text);

    for (String line : lines) {
      line = line.trim();

      // Skip separator lines (lines with only dashes, pipes, and spaces)
      if (line.matches("^[|\\-\\s+:]*$")) {
        continue;
      }

      if (line.isEmpty()) {
        continue;
      }

      // Remove leading and trailing pipes if present
      if (line.startsWith("|")) {
        line = line.substring(1);
      }
      if (line.endsWith("|")) {
        line = line.substring(0, line.length() - 1);
      }

      // Split by delimiter
      String[] cells;
      if (delimiter.equals("|")) {
        cells = line.split("\\|");
      } else if (delimiter.equals("\t")) {
        cells = line.split("\t");
      } else if (delimiter.equals(",")) {
        cells = line.split(",");
      } else {
        // Split by multiple spaces
        cells = line.split("\\s{2,}");
      }

      List<String> row = new ArrayList<>();
      for (String cell : cells) {
        row.add(cell.trim());
      }

      if (!row.isEmpty()) {
        rows.add(row);
      }
    }

    return rows;
  }

  private String detectDelimiter(String text) {
    // Count occurrences of potential delimiters
    long pipeCount = text.chars().filter(ch -> ch == '|').count();
    long tabCount = text.chars().filter(ch -> ch == '\t').count();
    long commaCount = text.chars().filter(ch -> ch == ',').count();

    // Pipe is most likely for markdown tables
    if (pipeCount > 0) {
      return "|";
    }

    // Tab for TSV
    if (tabCount > 0) {
      return "\t";
    }

    // Comma for CSV
    if (commaCount > 0) {
      return ",";
    }

    // Default to multiple spaces
    return "\\s+";
  }

  private String padRight(String text, int length) {
    if (text.length() >= length) {
      return text;
    }
    return text + " ".repeat(length - text.length());
  }

}
