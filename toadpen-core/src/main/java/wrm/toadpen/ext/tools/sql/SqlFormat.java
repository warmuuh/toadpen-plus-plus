package wrm.toadpen.ext.tools.sql;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import wrm.toadpen.core.tools.ToolManager;

@Singleton
@Named("tools.sql.format")
public class SqlFormat implements ToolManager.TextTool {

  @Override
  public void execute(ToolManager.TextContext context) {
    String sql = context.getSelectedText();
    context.replaceSelectedText(formatSql(sql));
  }

  private String formatSql(String sql) {
    String formatted = sql;

    // Add newlines before major keywords
    formatted = formatted.replaceAll("(?i)\\s+(SELECT|FROM|WHERE|GROUP BY|HAVING|ORDER BY|LIMIT|UNION|JOIN|LEFT JOIN|RIGHT JOIN|INNER JOIN|OUTER JOIN|ON|AND|OR)\\s+", "\n$1 ");

    // Add newlines after commas in SELECT clauses
    formatted = formatted.replaceAll(",\\s*(?=\\w)", ",\n  ");

    // Uppercase keywords
    Pattern pattern = Pattern.compile(
        "(?i)\\b(SELECT|FROM|WHERE|GROUP BY|HAVING|ORDER BY|LIMIT|UNION|JOIN|LEFT|RIGHT|INNER|OUTER|ON|AND|OR|AS|BY|ASC|DESC|INSERT|INTO|VALUES|UPDATE|SET|DELETE|CREATE|TABLE|INDEX|DROP|ALTER|ADD|COLUMN|PRIMARY|KEY|FOREIGN|REFERENCES|NULL|NOT|DEFAULT|AUTO_INCREMENT|VARCHAR|INT|DATE|DATETIME|TIMESTAMP|TEXT|BOOLEAN)\\b");
    Matcher matcher = pattern.matcher(formatted);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
        matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
    }
    matcher.appendTail(sb);
    formatted = sb.toString();

    // Trim leading/trailing whitespace
    formatted = formatted.trim();

    return formatted;
  }

}
