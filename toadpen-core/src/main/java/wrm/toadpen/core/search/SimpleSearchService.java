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
import java.util.Stack;
import java.util.Vector;
import java.util.function.Predicate;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import wrm.toadpen.core.util.FileUtil;

@Component
public class SimpleSearchService implements SearchService {

  private final Predicate<File> isTextFile = file -> {
    try {
      return !FileUtil.isBinaryFile(file);
    } catch (IOException e) {
      return false;
    }
  };

  private final Predicate<File> isIgnoredDirectory = file ->
    file.isDirectory() && file.getName().equals(".git")
  ;

  @Override
  @SneakyThrows
  public List<SearchResult> search(File rootDirectory, String query) {
    List<SearchResult> results = new ArrayList<>();
    Files.walkFileTree(rootDirectory.toPath(), new SimpleFileVisitor<Path>() {

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
            int lineNumber = 0;
            while (it.hasNext()) {
              lineNumber++;
              String line = it.next();
              int indexOfTerm = line.indexOf(query);
              if (indexOfTerm >= 0) {
                results.add(new SearchResult(file.toFile(), lineNumber, indexOfTerm + 1, line));
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
