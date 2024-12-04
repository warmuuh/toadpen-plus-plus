package wrm.toadpen.core.search;

import java.io.File;
import java.util.List;

public interface SearchService {
  List<SearchResult> search(File rootDirectory, String query);
}
