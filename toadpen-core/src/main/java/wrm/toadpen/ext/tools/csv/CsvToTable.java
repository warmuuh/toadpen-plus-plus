package wrm.toadpen.ext.tools.csv;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.csv.CsvToTable")
public class CsvToTable implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String csv = context.getSelectedText();
    String[] lines = csv.split("\n");

    if (lines.length == 0) {
      return;
    }

    // Parse all rows
    List<String[]> rows = new ArrayList<>();
    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.isEmpty()) {
        continue;
      }
      rows.add(parseCsvLine(trimmed));
    }

    if (rows.isEmpty()) {
      return;
    }

    // Calculate column widths
    int numColumns = rows.stream().mapToInt(row -> row.length).max().orElse(0);
    int[] columnWidths = new int[numColumns];

    for (String[] row : rows) {
      for (int i = 0; i < row.length; i++) {
        columnWidths[i] = Math.max(columnWidths[i], row[i].length());
      }
    }

    // Build ASCII table
    StringBuilder table = new StringBuilder();

    // Top border
    table.append(buildBorder(columnWidths, "┌", "┬", "┐"));
    table.append("\n");

    // Header row
    table.append(buildRow(rows.get(0), columnWidths));
    table.append("\n");

    // Header separator
    table.append(buildBorder(columnWidths, "├", "┼", "┤"));
    table.append("\n");

    // Data rows
    for (int i = 1; i < rows.size(); i++) {
      table.append(buildRow(rows.get(i), columnWidths));
      table.append("\n");
    }

    // Bottom border
    table.append(buildBorder(columnWidths, "└", "┴", "┘"));

    context.replaceSelectedText(table.toString());
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

  private String buildBorder(int[] columnWidths, String left, String middle, String right) {
    StringBuilder border = new StringBuilder();
    border.append(left);

    for (int i = 0; i < columnWidths.length; i++) {
      border.append("─".repeat(columnWidths[i] + 2));
      if (i < columnWidths.length - 1) {
        border.append(middle);
      }
    }

    border.append(right);
    return border.toString();
  }

  private String buildRow(String[] cells, int[] columnWidths) {
    StringBuilder row = new StringBuilder();
    row.append("│");

    for (int i = 0; i < columnWidths.length; i++) {
      String cell = i < cells.length ? cells[i] : "";
      row.append(" ").append(padRight(cell, columnWidths[i])).append(" │");
    }

    return row.toString();
  }

  private String padRight(String text, int length) {
    if (text.length() >= length) {
      return text;
    }
    return text + " ".repeat(length - text.length());
  }

}
