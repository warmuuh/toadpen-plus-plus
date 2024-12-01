package wrm.toadpen.core.watchdog;

import java.io.File;
import lombok.SneakyThrows;

public class FileHasher {
  @SneakyThrows
    public static String hashFile(File file) {
      //hash file content
      return MD5Checksum.getMD5Checksum(file.getAbsolutePath());
    }
}
