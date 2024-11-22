package wrm.asd.core.ui.macos;

import jakarta.inject.Singleton;
import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import lombok.experimental.UtilityClass;
import wrm.asd.core.util.PlatformUtil;

@UtilityClass
public class MacosSettings {

  public void init() {
    if (PlatformUtil.getOS().equals("mac")) {
      System.getProperties().put("apple.laf.useScreenMenuBar", "true");
      System.setProperty( "apple.awt.application.appearance", "system" );
      System.setProperty( "apple.awt.application.name", "ToadPen++" ); //TODO: doesnt work
      new MacImpl();
    }
  }



  class MacImpl implements AboutHandler, PreferencesHandler,
      QuitHandler {

    public MacImpl() {
      handleOS();
    }

    public void handleOS() {
      try {
        final Desktop desktop = Desktop.getDesktop();
        desktop.setAboutHandler(this);
        desktop.setPreferencesHandler(this);
        desktop.setQuitHandler(this);
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }

    @Override
    public void handleAbout(AboutEvent arg0) {
      // new AboutDialog();
      System.out.println("handleAbout()");
    }

    @Override
    public void handlePreferences(PreferencesEvent arg0) {
      // new OptionsDialog();
      System.out.println("handlePreferences()");
    }

    @Override
    public void handleQuitRequestWith(QuitEvent arg0, QuitResponse arg1) {
      System.exit(0);
    }
  }

}
