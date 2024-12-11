package wrm.toadpen.core.ui.dialogs;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.DefaultEventListModel;
import ca.odell.glazedlists.swing.GlazedListsSwing;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import wrm.toadpen.core.search.SearchResult;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.components.PlaceholderTextField;

public class SearchBox<T> {

  private final JDialog dialog;
  private final PlaceholderTextField inputField;
  private final JList<T> listField;
  private Function<T, String> itemToStringFn = Object::toString;
  private BiConsumer<JComponent, T> renderItemFn = (parent, itm) -> parent.add(new JLabel(itm.toString()));

  private T chosenItem;

  public SearchBox(String placeholder, Collection<T> items) {
    dialog = new JDialog(MainWindow.getFrame(), "Searchbox",
        Dialog.ModalityType.APPLICATION_MODAL);
    dialog.setUndecorated(true);
    dialog.setLocationRelativeTo(MainWindow.getFrame());
    Container dialogContainer = dialog.getContentPane();
    dialogContainer.setLayout(new BorderLayout());

    this.inputField = new PlaceholderTextField();
    this.inputField.setPlaceholder(placeholder);
    this.inputField.setSize(300, 30);

    EventList<T> source = GlazedLists.eventList(items);
    MatcherEditor<T> textMatcherEditor = new TextComponentMatcherEditor<>(
        inputField, (baseList, element) -> baseList.add(itemToStringFn.apply(element)));

    FilterList<T> filteredList = new FilterList<>(source, textMatcherEditor);
    DefaultEventListModel<T> listModel = GlazedListsSwing.eventListModel(filteredList);

    this.listField = new JList<>(listModel);
    this.listField.setCellRenderer(new SearchboxListCellRenderer());
    listField.setSelectedIndex(0);
    //double click listener
    listField.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
          selectItemAndClose();
        }
      }
    });

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.add(listField);
    scrollPane.setSize(300, 200);

    dialogContainer.add(inputField, BorderLayout.NORTH);
    dialogContainer.add(scrollPane, BorderLayout.CENTER);
    dialog.pack();

    setupNavigationKeys();
    setupEnterKey();
    setupEscapeKey();
  }

  private void setupNavigationKeys() {
    inputField.getInputMap(JComponent.WHEN_FOCUSED).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "searchbox.down"
    );
    inputField.getActionMap().put("searchbox.down", new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
            listField.setSelectedIndex(listField.getSelectedIndex() + 1);
          }
        }
    );

    inputField.getInputMap(JComponent.WHEN_FOCUSED).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "searchbox.up"
    );
    inputField.getActionMap().put("searchbox.up", new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
            listField.setSelectedIndex(listField.getSelectedIndex() - 1);
          }
        }
    );
  }

  private void setupEnterKey() {
    inputField.getInputMap(JComponent.WHEN_FOCUSED).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "searchbox.confirm"
    );
    inputField.getActionMap().put("searchbox.confirm", new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
            selectItemAndClose();
          }
        }
    );
  }

  private void selectItemAndClose() {
    chosenItem = listField.getSelectedValue();
    dialog.dispatchEvent(new WindowEvent(
        dialog, WindowEvent.WINDOW_CLOSING
    ));
  }

  private void setupEscapeKey() {
    JRootPane root = dialog.getRootPane();
    root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
        KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "searchbox.cancel"
    );
    root.getActionMap().put("searchbox.cancel", new AbstractAction() {
          public void actionPerformed(ActionEvent event) {
            dialog.dispatchEvent(new WindowEvent(
                dialog, WindowEvent.WINDOW_CLOSING
            ));
          }
        }
    );
  }

  public void setItemToStringFn(Function<T, String> itemToStringFn) {
    this.itemToStringFn = itemToStringFn;
  }

  public void setRenderItemFn(BiConsumer<JComponent, T> renderItemFn) {
    this.renderItemFn = renderItemFn;
  }

  public T showDialog() {
    dialog.setVisible(true);
    return chosenItem;
  }


  private class SearchboxListCellRenderer extends JPanel implements ListCellRenderer<T> {

    public SearchboxListCellRenderer() {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends T> list,
                                                  T entry, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
      removeAll();
      renderItemFn.accept(this, entry);

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
