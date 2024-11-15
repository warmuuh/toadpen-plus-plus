package wrm.asd.core.util;

public class PlatformUtil {

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
