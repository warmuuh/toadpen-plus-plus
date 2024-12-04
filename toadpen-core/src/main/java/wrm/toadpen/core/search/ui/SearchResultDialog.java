package wrm.toadpen.core.search.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Function;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import wrm.toadpen.core.search.SearchResult;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.components.PlaceholderTextField;

public class SearchResultDialog {

  private final JDialog dialog;
  private final PlaceholderTextField inputField;
  private final JButton searchButton;
  private final JList<SearchResult> listField;

  private SearchResult selectedResult;


  public SearchResultDialog(String placeholder, Function<String, List<SearchResult>> searchFunction) {
    dialog = new JDialog(MainWindow.getFrame(), "Search everywhere",
        Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setLocationRelativeTo(MainWindow.getFrame());
    Container dialogContainer = dialog.getContentPane();
    dialogContainer.setLayout(new BorderLayout());



    this.inputField = new PlaceholderTextField();
    this.inputField.setPlaceholder(placeholder);
    this.inputField.setSize(300, 30);

    this.searchButton = new JButton("Search");
    searchButton.addActionListener(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        List<SearchResult> results = searchFunction.apply(inputField.getText());
        listField.setListData(results.toArray(new SearchResult[0]));
      }
    });

    JPanel searchForm = new JPanel();
    searchForm.setLayout(new BoxLayout(searchForm, BoxLayout.X_AXIS));
    searchForm.add(inputField);
    searchForm.add(searchButton);


    this.listField = new JList<>();

    listField.setCellRenderer(new SearchResultRenderer());

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.add(listField);
    scrollPane.setSize(300, 200);

    dialogContainer.add(searchForm, BorderLayout.NORTH);
    dialogContainer.add(scrollPane, BorderLayout.CENTER);
    dialog.pack();

    setupDoubleClickListener();

  }

  private void setupDoubleClickListener() {
    listField.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent evt) {
        JList<SearchResult> list = (JList<SearchResult>)evt.getSource();
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
          // Double-click detected
          int index = list.locationToIndex(evt.getPoint());
          selectedResult = list.getModel().getElementAt(index);
          dialog.setVisible(false);
        }
      }
    });
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
    public Component getListCellRendererComponent(JList<? extends SearchResult> list, SearchResult entry, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

      removeAll();
      add(new JLabel(entry.getLineContent()));
      add(Box.createHorizontalGlue());
      add(new JLabel(entry.getFile().getName()+":"+entry.getLine()));

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
