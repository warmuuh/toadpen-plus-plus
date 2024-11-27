package wrm.toadpen.core.behavior;

import jakarta.inject.Singleton;
import java.awt.desktop.QuitResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import wrm.toadpen.core.ui.MainWindow;

@Singleton
@RequiredArgsConstructor
public class ShutdownBehavior {

  private final MainWindow mainWindow;


  public void shutdown(@Nullable QuitResponse response) {
    if (mainWindow.canCloseAllEditors()) {
      if (response != null) {
        response.performQuit();
      }
      System.exit(0);
    } else {
      if (response != null) {
        response.cancelQuit();
      }
    }
  }
}
