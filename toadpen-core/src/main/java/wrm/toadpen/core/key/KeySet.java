package wrm.toadpen.core.key;

import jakarta.inject.Singleton;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Map;
import javax.swing.KeyStroke;
import org.jspecify.annotations.Nullable;
import wrm.toadpen.core.cmd.ApplicationCommands;
import wrm.toadpen.core.cmd.EditorCommands;
import wrm.toadpen.core.cmd.FileCommands;

@Singleton
public class KeySet {
  private final Map<String, KeyStroke> keyMap = Map.of(
      FileCommands.FILE_NEW, ks(KeyEvent.VK_N, Modifier.CTRL),
      FileCommands.FILE_OPEN, ks(KeyEvent.VK_O, Modifier.CTRL),
      FileCommands.FILE_SAVE, ks(KeyEvent.VK_S, Modifier.CTRL),
      FileCommands.FILE_FIND, ks(KeyEvent.VK_R, Modifier.CTRL | Modifier.SHIFT),
      FileCommands.FILE_RECENT, ks(KeyEvent.VK_E, Modifier.CTRL),
      EditorCommands.EDITOR_SEARCH, ks(KeyEvent.VK_F, Modifier.CTRL),
      ApplicationCommands.APPLICATION_TOGGLE_TERMINAL, ks(KeyEvent.VK_T, Modifier.CTRL | Modifier.SHIFT),
      ApplicationCommands.APPLICATION_SEARCH_EVERYWHERE, ks(KeyEvent.VK_H, Modifier.CTRL | Modifier.SHIFT),
      ApplicationCommands.APPLICATION_SEARCH_COMMAND, ks(KeyEvent.VK_3, Modifier.CTRL)
  );

  public @Nullable KeyStroke getKeystrokeForCommand(String command) {
    return keyMap.get(command);
  }


  public static KeyStroke ks(int key, int modifiers) {
    int awtModifiers = 0;
    if ((modifiers & Modifier.CTRL) != 0) {
      awtModifiers |= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    }
    if ((modifiers & Modifier.SHIFT) != 0) {
      awtModifiers |= InputEvent.SHIFT_MASK;
    }
    if ((modifiers & Modifier.ALT) != 0) {
      awtModifiers |= InputEvent.ALT_MASK;
    }

    return KeyStroke.getKeyStroke(key, awtModifiers);
  }

  private interface Modifier {
    int CTRL = 1 << 0;
    int SHIFT = 1 << 1;
    int ALT = 1 << 2;
    int META = 1 << 3;
  }
}
