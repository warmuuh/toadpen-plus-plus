package wrm.asd.core.ui.menu;

import io.avaje.inject.PostConstruct;
import jakarta.inject.Singleton;
import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import wrm.asd.core.util.PlatformUtil;

@Singleton
public class Menu {

  private JMenuBar mb;

  public JMenuBar getMenuBar() {
    return mb;
  }



  @PostConstruct
  JMenuBar createMenuBar() {
    if (PlatformUtil.getOS().equals("mac")) {
      System.getProperties().put("apple.laf.useScreenMenuBar", "true");
      System.setProperty( "apple.awt.application.appearance", "system" );
      System.setProperty( "apple.awt.application.name", "ToadPen++" ); //TODO: doesnt work
      new MacImpl();
    }
    mb = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem quitItem = new JMenuItem("Quit");
    quitItem.addActionListener(e -> System.exit(0));
    fileMenu.add(quitItem);
    mb.add(fileMenu);


    return mb;
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
