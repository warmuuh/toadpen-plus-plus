package wrm.toadpen.core.ui.options;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import wrm.toadpen.ai.AiOptions;

public class AiOptionsPanel extends JPanel {
  private final AiOptions aiOptions;

  public AiOptionsPanel(AiOptions aiOptions) {
    this.aiOptions = aiOptions;
    setLayout(new MigLayout());
    createModelFilePanel();
    doLayout();
  }

  private void createModelFilePanel() {
    JLabel sectionHeading = new JLabel("Ai Model Configuration");
    sectionHeading.setFont(new Font("Arial", Font.BOLD, 14));
    add(sectionHeading);
    add(new JSeparator(), new CC().span().growX());

    add(new JLabel("Model file:"));
    JTextField modelFileField = new JTextField();
    modelFileField.setText(aiOptions.getModelFile());
    add(modelFileField);
    modelFileField.addActionListener(e -> aiOptions.setModelFile(modelFileField.getText()));

    JButton fileChoose = new JButton("Choose...");
    add(fileChoose);
    fileChoose.addActionListener(e -> {
      FileDialog dialog =
          new FileDialog((Frame) null, "Choose model file", FileDialog.LOAD);
      dialog.setVisible(true);
      if (dialog.getFile() != null) {
        modelFileField.setText(dialog.getDirectory() + dialog.getFile());
        aiOptions.setModelFile(modelFileField.getText());
      }
    });



  }
}
