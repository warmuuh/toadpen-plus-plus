package wrm.toadpen.core.ui.options;

import jakarta.inject.Singleton;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.ai.AiOptions;
import wrm.toadpen.core.ui.MainWindow;

@Singleton
@RequiredArgsConstructor
public class OptionsDialog {
  private final AiOptions aiOptions;

  public void show() {
    JDialog dialog = new JDialog(MainWindow.getFrame());
    dialog.setTitle("Options");

    JTabbedPane tabs = new JTabbedPane();
    tabs.setPreferredSize(new Dimension(600, 400));
    dialog.getContentPane().add(tabs);


    AiOptionsPanel aiOptionsPanel = new AiOptionsPanel(aiOptions);
    tabs.addTab("AI", aiOptionsPanel);


    tabs.addTab("Other", new JPanel());

    dialog.pack();
    dialog.setVisible(true);
  }
}
