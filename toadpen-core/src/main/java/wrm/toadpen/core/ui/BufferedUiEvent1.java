package wrm.toadpen.core.ui;

public class BufferedUiEvent1<T> extends UiEvent1<T>{
    private T valueBuffer;


    public void fire(T arg) {
        if (listeners.isEmpty()) {
            valueBuffer = arg;
        } else {
            valueBuffer = null;
            super.fire(arg);
        }
    }

    public void clear() {
        valueBuffer = null;
        super.clear();
    }

    public boolean hasValueBuffer() {
        return valueBuffer != null;
    }

    public T fetchValueBuffer() {
        T tmp = valueBuffer;
        valueBuffer = null;
        return tmp;
    }
}
