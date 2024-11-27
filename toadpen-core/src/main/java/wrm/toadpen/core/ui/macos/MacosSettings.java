package wrm.toadpen.core.ui.macos;

import lombok.experimental.UtilityClass;
import wrm.toadpen.core.util.PlatformUtil;

@UtilityClass
public class MacosSettings {

  public void init() {
    if (PlatformUtil.getOS().equals("mac")) {
      System.getProperties().put("apple.laf.useScreenMenuBar", "true");
      System.setProperty( "apple.awt.application.appearance", "system" );
      System.setProperty( "apple.awt.application.name", "Toadpen++" );
    }
  }

}
