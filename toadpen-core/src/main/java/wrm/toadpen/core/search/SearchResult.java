package wrm.toadpen.core.search;

import java.io.File;
import lombok.Value;

@Value
public class SearchResult {
  File file;
  int line;
  int column;
  String lineContent;

  public String toString() {
    return file.getName() + " : " + line;
  }
}
