package wrm.asd.core.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * used for "small-scope" events, not application-wide events
 */
public class UiEvent1<T> {
  private List<Consumer<T>> listeners = new LinkedList<>();

  public void addListener(Consumer<T> listener) {
    listeners.add(listener);
  }

  public void removeListener(Consumer<T> listener) {
    listeners.remove(listener);
  }

  public void fire(T arg) {
    listeners.forEach(l -> l.accept(arg));
  }

  public void clear() {
    listeners.clear();
  }

}
