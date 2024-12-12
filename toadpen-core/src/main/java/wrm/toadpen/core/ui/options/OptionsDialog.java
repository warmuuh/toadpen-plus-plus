package wrm.toadpen.core.ui.options;

import jakarta.inject.Singleton;
import javax.swing.JDialog;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.ai.AiOptions;

@Singleton
@RequiredArgsConstructor
public class OptionsDialog {
  private final AiOptions aiOptions;

  public void show() {
    JDialog dialog = new JDialog();
    dialog.setTitle("Options");
    dialog.setSize(400, 300);
    dialog.getContentPane().add(new AiOptionsPanel(aiOptions));
    dialog.pack();
    dialog.setVisible(true);
  }
}
