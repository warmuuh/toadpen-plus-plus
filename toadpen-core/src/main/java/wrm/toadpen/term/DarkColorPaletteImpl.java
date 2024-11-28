package wrm.toadpen.term;

import com.jediterm.core.Color;
import com.jediterm.terminal.emulator.ColorPalette;
import org.jetbrains.annotations.NotNull;

public class DarkColorPaletteImpl extends ColorPalette {

  private static final Color[] DARK_XTERM_COLORS = new Color[]{
      new Color(0xe5e5e5), //Black -> White
      new Color(0xcd0000), //Red
      new Color(0x00cd00), //Green
      new Color(0xcdcd00), //Yellow
      new Color(0x1e90ff), //Blue
      new Color(0xcd00cd), //Magenta
      new Color(0x00cdcd), //Cyan
      new Color(0x000000), //White -> Black
      //Bright versions of the ISO colors
      new Color(0xffffff), //Black -> White
      new Color(0xff0000), //Red
      new Color(0x00ff00), //Green
      new Color(0xffff00), //Yellow
      new Color(0x4682b4), //Blue
      new Color(0xff00ff), //Magenta
      new Color(0x00ffff), //Cyan
      new Color(0x4c4c4c), //White -> Black
  };

  public static final ColorPalette DARK_XTERM_PALETTE = new DarkColorPaletteImpl(DARK_XTERM_COLORS);

  private static final Color[] DARK_WINDOWS_COLORS = new Color[]{
      new Color(0x000000), //Black
      new Color(0x800000), //Red
      new Color(0x008000), //Green
      new Color(0x808000), //Yellow
      new Color(0x000080), //Blue
      new Color(0x800080), //Magenta
      new Color(0x008080), //Cyan
      new Color(0xc0c0c0), //White
      //Bright versions of the ISO colors
      new Color(0x808080), //Black
      new Color(0xff0000), //Red
      new Color(0x00ff00), //Green
      new Color(0xffff00), //Yellow
      new Color(0x4682b4), //Blue
      new Color(0xff00ff), //Magenta
      new Color(0x00ffff), //Cyan
      new Color(0xffffff), //White
  };

  public static final ColorPalette DARK_WINDOWS_PALETTE = new DarkColorPaletteImpl(DARK_WINDOWS_COLORS);

  private final Color[] myColors;

  private DarkColorPaletteImpl(@NotNull Color[] colors) {
    myColors = colors;
  }

  @NotNull
  @Override
  public Color getForegroundByColorIndex(int colorIndex) {
    return myColors[colorIndex];
  }

  @NotNull
  @Override
  protected Color getBackgroundByColorIndex(int colorIndex) {
    return myColors[colorIndex];
  }
}
