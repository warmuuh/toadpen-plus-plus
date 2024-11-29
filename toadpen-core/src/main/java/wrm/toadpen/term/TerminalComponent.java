package wrm.toadpen.term;

import com.jediterm.terminal.ProcessTtyConnector;
import com.jediterm.terminal.emulator.ColorPalette;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider;
import com.jthemedetecor.OsThemeDetector;
import com.pty4j.PtyProcessBuilder;
import io.avaje.inject.Component;
import io.avaje.inject.PostConstruct;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import javafx.application.Platform;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import lombok.SneakyThrows;
import wrm.toadpen.core.util.PlatformUtil;

@Component
public class TerminalComponent {

  JPanel panel;
  JediTermWidget terminal;
  private Process process;
  private ProcessTtyConnector ttyConnector;

  @PostConstruct
  public void init() {
    panel = new JPanel();
    panel.setLayout(new BorderLayout());
  }

  @SneakyThrows
  public void start(File file) {
    stopIfRunning();
    DefaultSettingsProvider settings = new DefaultSettingsProvider() {
      @Override
      public ColorPalette getTerminalColorPalette() {
        if (OsThemeDetector.getDetector().isDark()) {
          if (PlatformUtil.getOS().equals("win")){
            return DarkColorPaletteImpl.DARK_WINDOWS_PALETTE;
          } else {
            return DarkColorPaletteImpl.DARK_XTERM_PALETTE;
          }
        }
        return super.getTerminalColorPalette();
      }
    };
    terminal = new JediTermWidget(settings);

    panel.add(terminal, BorderLayout.CENTER);

    File dir = file.isDirectory() ? file : file.getParentFile();
    process = new PtyProcessBuilder()
        .setCommand(new String[] {"/bin/bash", "-l"})
        .setEnvironment(System.getenv())
        .setConsole(false)
        .setUseWinConPty(true)
        .setDirectory(dir.getAbsolutePath())
        .start();

    ttyConnector = new ProcessTtyConnector(process, StandardCharsets.UTF_8) {
      @Override
      public String getName() {
        return "Term";
      }
    };
    terminal.setTtyConnector(ttyConnector);
    terminal.start();
  }

  public void stopIfRunning() {
    if (isRunning()) {
      terminal.close();
      panel.remove(terminal);
    }
  }

  public boolean isRunning() {
    return terminal != null && terminal.isSessionRunning();
  }

  public JComponent getComponent() {
    return panel;
  }

}
