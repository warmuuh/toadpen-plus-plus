package wrm.toadpen.core.ui.options;

import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import wrm.toadpen.core.ui.editor.EditorOptions;

public class EditorOptionsPanel extends JPanel {
  private final EditorOptions editorOptions;

  public EditorOptionsPanel(EditorOptions editorOptions) {
    this.editorOptions = editorOptions;
    setLayout(new MigLayout());
    createOptionsPanel();
    doLayout();
  }

  private void createOptionsPanel() {
    JLabel sectionHeading = new JLabel("General Options");
    sectionHeading.setFont(new Font("Arial", Font.BOLD, 14));
    add(sectionHeading);
    add(new JSeparator(), new CC().span().growX());

    add(new JLabel("Tab width:"));
    JSpinner tabWidth = new JSpinner(new SpinnerNumberModel(editorOptions.getTabSize(), 2, 8, 1));
    add(tabWidth, "wrap");
    tabWidth.addChangeListener(e -> editorOptions.setTabSize((Integer)tabWidth.getValue()));

    add(new JLabel("Show whitespaces:"));
    JCheckBox showWhitespaces = new JCheckBox();
    add(showWhitespaces, "wrap");
    showWhitespaces.setSelected(editorOptions.isShowWhitespaces());
    showWhitespaces.addChangeListener(e -> editorOptions.setShowWhitespaces(showWhitespaces.isSelected()));

  }
}
