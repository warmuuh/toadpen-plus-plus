package wrm.toadpen.core.ui.options;

import javax.swing.JPanel;
import javax.swing.JTextField;
import wrm.toadpen.ai.AiOptions;

public class AiOptionsPanel extends JPanel {
  private final AiOptions aiOptions;

  public AiOptionsPanel(AiOptions aiOptions) {
    this.aiOptions = aiOptions;
    setLayout(new java.awt.GridLayout(1, 2));
    createModelFilePanel();
  }

  private void createModelFilePanel() {
    add(new javax.swing.JLabel("Model file:"));
    JTextField modelFileField = new JTextField();
    modelFileField.setText(aiOptions.getModelFile());
    add(modelFileField);
    modelFileField.addActionListener(e -> aiOptions.setModelFile(modelFileField.getText()));
  }
}
