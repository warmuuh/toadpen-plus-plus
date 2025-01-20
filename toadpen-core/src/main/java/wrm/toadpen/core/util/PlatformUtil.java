package wrm.toadpen.core.util;

import java.nio.file.Paths;
import org.apache.commons.lang3.SystemUtils;

public class PlatformUtil {

  public static String getApplicationFileDirectory(String filename) {
//    if (Files.isWritable(new File(".").toPath())) {
//      return filename;
//    }
    return getOsSpecificAppDataFolder(filename);
  }

  private static String getOsSpecificAppDataFolder(String filename) {
    if (SystemUtils.IS_OS_WINDOWS) {
      return Paths.get(System.getenv("LOCALAPPDATA"), "Toadpen", filename).toString();
    } else if (SystemUtils.IS_OS_MAC) {
      return Paths.get(System.getProperty("user.home"), "Library", "Application Support", "Toadpen", filename).toString();
    } else if (SystemUtils.IS_OS_LINUX) {
      return Paths.get(System.getProperty("user.home"), ".toad", filename).toString();
    }

    return filename;
  }

  public static String getOS() {
    String osname = System.getProperty("os.name");
    if (osname != null && osname.toLowerCase().indexOf("mac") != -1) {
      return "mac";
    }
    if (osname != null && osname.toLowerCase().indexOf("windows") != -1) {
      return "win";
    }
    return "noarch";
  }

}
