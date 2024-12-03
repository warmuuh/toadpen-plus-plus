package wrm.toadpen.core.search;

import java.io.File;
import lombok.Value;

@Value
public class SearchResult {
  File file;
  int line;
  String context;
}
