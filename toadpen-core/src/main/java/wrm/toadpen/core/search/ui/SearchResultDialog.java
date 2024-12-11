package wrm.toadpen.core.search.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Function;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import org.apache.commons.io.IOUtils;
import wrm.toadpen.core.search.SearchResult;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.components.PlaceholderTextField;
import wrm.toadpen.core.ui.editor.EditorComponent;
import wrm.toadpen.core.ui.editor.EditorFactory;

public class SearchResultDialog {

  private final JDialog dialog;
  private final PlaceholderTextField inputField;
  private final JButton searchButton;
  private final JList<SearchResult> listField;
  private final JPanel detailsBox;

  private final Function<String, List<SearchResult>> searchFunction;
  private final EditorFactory editorFactory;

  private SearchResult selectedResult;


  public SearchResultDialog(String placeholder,
                            Function<String, List<SearchResult>> searchFunction,
                            EditorFactory editorFactory) {
    this.searchFunction = searchFunction;
    this.editorFactory = editorFactory;
    dialog = new JDialog(MainWindow.getFrame(), "Search everywhere",
        Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setLocationRelativeTo(MainWindow.getFrame());
    Container dialogContainer = dialog.getContentPane();
    dialogContainer.setLayout(new BorderLayout());


    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent e) {
        selectedResult = null;
      }
    });
    //setup esc key
    dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
    dialog.getRootPane().getActionMap().put("close", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        selectedResult = null;
        dialog.setVisible(false);

      }
    });

    detailsBox = new JPanel();
    detailsBox.setLayout(new BorderLayout());
    detailsBox.setPreferredSize(new java.awt.Dimension(600, 200));

    this.inputField = new PlaceholderTextField();
    this.inputField.setPlaceholder(placeholder);
    this.inputField.setSize(600, 30);

    SwingUtilities.invokeLater(inputField::requestFocusInWindow);
    this.searchButton = new JButton("Search");

    inputField.getInputMap(JButton.WHEN_FOCUSED)
        .put(KeyStroke.getKeyStroke("ENTER"), "searchresult.search");
    inputField.getActionMap().put("searchresult.search", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applySearchFilter();
      }
    });

    searchButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        applySearchFilter();
      }
    });

    JPanel searchForm = new JPanel();
    searchForm.setLayout(new BoxLayout(searchForm, BoxLayout.X_AXIS));
    searchForm.add(inputField);
    searchForm.add(searchButton);


    this.listField = new JList<>();

    listField.getInputMap(JButton.WHEN_FOCUSED)
        .put(KeyStroke.getKeyStroke("ENTER"), "searchresult.choose");
    listField.getActionMap().put("searchresult.choose", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        chooseSelectedItem();
      }
    });

    listField.setCellRenderer(new SearchResultRenderer());
    listField.addListSelectionListener(e -> {
      if (!e.getValueIsAdjusting()) {
        if (selectedResult != listField.getSelectedValue()) {
          selectedResult = listField.getSelectedValue();
          updateDetailsBox();
        }
      }
    });

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.add(listField);
    scrollPane.setSize(600, 200);

    dialogContainer.add(searchForm, BorderLayout.NORTH);

    JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    split.setTopComponent(scrollPane);
    split.setBottomComponent(detailsBox);
    dialogContainer.add(split, BorderLayout.CENTER);
    dialog.pack();

    setupDoubleClickListener();

  }

  private void applySearchFilter() {
    List<SearchResult> results = searchFunction.apply(inputField.getText());
    listField.setListData(results.toArray(new SearchResult[0]));
    listField.grabFocus();
    listField.setSelectedIndex(0);
  }

  private void updateDetailsBox() {
    detailsBox.removeAll();
    if (selectedResult != null) {
      try {
        File file = selectedResult.getFile();
        String text =
            IOUtils.toString(file.toURI(), Charset.defaultCharset());
        EditorComponent editor =
            editorFactory.createEditor(selectedResult.getFile(), text);
        editor.setEditable(false);
        JPanel jPanel = new JPanel();
        jPanel.add(editor.getComponent());
        detailsBox.add(editor.getComponent(), BorderLayout.CENTER);
        detailsBox.revalidate();
        detailsBox.repaint();
        SwingUtilities.invokeLater(
            () -> editor.goToLine(selectedResult.getLine(), selectedResult.getColumn()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void setupDoubleClickListener() {
    listField.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
          // Double-click detected
          chooseSelectedItem();
        }
      }
    });
  }

  private void chooseSelectedItem() {
    int index = listField.getSelectedIndex();
    selectedResult = listField.getModel().getElementAt(index);
    dialog.setVisible(false);
  }


  public SearchResult showDialog() {
    dialog.setVisible(true);
    return selectedResult;
  }

  public class SearchResultRenderer extends JPanel implements ListCellRenderer<SearchResult> {

    public SearchResultRenderer() {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends SearchResult> list,
                                                  SearchResult entry, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

      removeAll();
      //trim linecontent around match
      String lineContent = entry.getLineContent();
      int start = Math.max(0, entry.getColumn() - 20);
      int end = Math.min(lineContent.length(), entry.getColumn() + 20);
      add(new JLabel(lineContent.substring(start, end)));
      add(Box.createHorizontalGlue());
      add(new JLabel(entry.getFile().getName() + ":" + entry.getLine()));

      setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
      setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
      for (Component component : getComponents()) {
        component.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
        component.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
      }

      return this;
    }
  }
}
