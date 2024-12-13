package wrm.toadpen.core.ui.options;

import jakarta.inject.Singleton;
import java.awt.Dimension;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.ai.AiOptions;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.editor.EditorOptions;

@Singleton
@RequiredArgsConstructor
public class OptionsDialog {
  private final AiOptions aiOptions;
  private final EditorOptions editorOptions;

  public void show() {
    JDialog dialog = new JDialog(MainWindow.getFrame());
    dialog.setTitle("Options");

    JTabbedPane tabs = new JTabbedPane();
    tabs.setPreferredSize(new Dimension(600, 400));
    dialog.getContentPane().add(tabs);


    EditorOptionsPanel editorOptionsPanel = new EditorOptionsPanel(editorOptions);
    tabs.addTab("Editor", editorOptionsPanel);


    AiOptionsPanel aiOptionsPanel = new AiOptionsPanel(aiOptions);
    tabs.addTab("AI", aiOptionsPanel);

    dialog.pack();
    dialog.setVisible(true);
  }
}
