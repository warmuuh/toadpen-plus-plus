package wrm.toadpen.core.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class BufferedUiEvent1<T> {
    private T valueBuffer;
    private List<Consumer<T>> listeners = new LinkedList<>();

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
        if (valueBuffer != null) {
            listener.accept(valueBuffer);
            valueBuffer = null;
        }
    }

    public void removeListener(Consumer<T> listener) {
        listeners.remove(listener);
    }

    public void fire(T arg) {
        if (listeners.isEmpty()) {
            valueBuffer = arg;
        } else {
            valueBuffer = null;
            listeners.forEach(l -> l.accept((T) arg));
        }
    }

    public void clear() {
        valueBuffer = null;
        listeners.clear();
    }
}
