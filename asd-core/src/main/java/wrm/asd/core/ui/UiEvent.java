package wrm.asd.core.ui;

import java.util.LinkedList;
import java.util.List;

/**
 * used for "small-scope" events, not application-wide events
 */
public class UiEvent {
  private List<Runnable> listeners = new LinkedList<>();

  public void addListener(Runnable listener) {
    listeners.add(listener);
  }

  public void removeListener(Runnable listener) {
    listeners.remove(listener);
  }

  public void fire() {
    listeners.forEach(Runnable::run);
  }

  public void clear() {
    listeners.clear();
  }

}
