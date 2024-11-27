package wrm.toadpen.core.ui.os;

import io.avaje.inject.Component;
import java.awt.Desktop;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.PreferencesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.io.File;
import wrm.toadpen.core.ui.BufferedUiEvent1;
import wrm.toadpen.core.ui.UiEvent1;

@Component
public class OsHandler implements AboutHandler, PreferencesHandler,
    QuitHandler {

  public BufferedUiEvent1<File> OnOpenFile = new BufferedUiEvent1<>();
  public UiEvent1<QuitResponse> OnQuitRequest = new UiEvent1<>();

  public OsHandler() {
    try {
      final Desktop desktop = Desktop.getDesktop();
      desktop.setOpenURIHandler(e -> OnOpenFile.fire(new File(e.getURI())));
      desktop.setOpenFileHandler(e -> OnOpenFile.fire(e.getFiles().getFirst()));
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
  public void handleQuitRequestWith(QuitEvent evt, QuitResponse res) {
    OnQuitRequest.fire(res);
  }
}
