package wrm.asd.core.key;

import jakarta.inject.Singleton;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Map;
import javax.swing.KeyStroke;
import org.jspecify.annotations.Nullable;

@Singleton
public class KeySet {
  private final Map<String, KeyStroke> keyMap = Map.of(
      "file.new", ks(KeyEvent.VK_N, Modifier.CTRL),
      "file.open", ks(KeyEvent.VK_O, Modifier.CTRL),
      "file.save", ks(KeyEvent.VK_S, Modifier.CTRL),
      "editor.search", ks(KeyEvent.VK_F, Modifier.CTRL)
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
