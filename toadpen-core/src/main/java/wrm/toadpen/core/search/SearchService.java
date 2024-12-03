package wrm.toadpen.core.search;

import io.avaje.inject.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

@Component
public class SearchService {

  private final Predicate<File> isTextFile = file -> {
    try {
      return Files.probeContentType(file.toPath()).equals("text/plain");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  };

  private final Predicate<File> isIgnoredDirectory = file ->
    file.isDirectory() && file.getName().equals(".git")
  ;

  @SneakyThrows
  public List<SearchResult> search(File root, String query) {
    List<SearchResult> results = new ArrayList<>();
    Files.walkFileTree(root.toPath(), new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException {
        if (isIgnoredDirectory.test(dir.toFile())) {
          return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (isTextFile.test(file.toFile())) {
          try (LineIterator it = FileUtils.lineIterator(file.toFile(), "UTF-8")) {
            while (it.hasNext()) {
              String line = it.next();
              if (line.contains(query)) {
                results.add(new SearchResult(file.toFile(), 0, line));
              }
            }
          }
        }

        return FileVisitResult.CONTINUE;
      }
    });

    return results;
  }

}
