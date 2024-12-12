package wrm.toadpen.core.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;

public interface Storage {

  void storeInteger(String key, Integer value);

  Integer loadInteger(String key, Integer defaultValue);

  void storeString(String key, String value);

  String loadString(String key, String defaultValue);

  void storeBoolean(String key, Boolean value);

  Boolean loadBoolean(String key, Boolean defaultValue);

  void storeDouble(String key, Double value);

  Double loadDouble(String key, Double defaultValue);

  void storeLong(String key, Long value);

  Long loadLong(String key, Long defaultValue);

  void storeList(String key, List<String> value);

  List<String> loadList(String key, List<String> defaultValue);

  void close();


  void setStorageListener(StorageListener listener);

  void removeStorageListener(StorageListener listener);

  interface StorageListener {
    void onStorageChanged(String key, Object value);
  }

  @RequiredArgsConstructor
  class StorageProperty<T> implements StorageListener {
    private final String key;
    private final T defaultValue;
    private final BiFunction<String, T, T> getter;
    private final BiConsumer<String, T> setter;
    private final List<Consumer<T>> changeListener = new ArrayList<>();

    public T get() {
      return getter.apply(key, defaultValue);
    }

    public void set(T value) {
      setter.accept(key, value);
    }

    public void addChangeListener(Consumer<T> listener) {
      changeListener.add(listener);
    }

    @Override
    public void onStorageChanged(String key, Object value) {
      if (key.equals(this.key)) {
        changeListener.forEach(listener -> listener.accept((T) value));
      }
    }
  }

  default StorageProperty<Integer> intProperty(String key, Integer defaultValue) {
    StorageProperty<Integer> property =
        new StorageProperty<>(key, defaultValue, this::loadInteger, this::storeInteger);
    setStorageListener(property);
    return property;
  }

  default StorageProperty<String> stringProperty(String key, String defaultValue) {
    StorageProperty<String> property =
        new StorageProperty<>(key, defaultValue, this::loadString, this::storeString);
    setStorageListener(property);
    return property;
  }

  default StorageProperty<Boolean> booleanProperty(String key, Boolean defaultValue) {
    StorageProperty<Boolean> property =
        new StorageProperty<>(key, defaultValue, this::loadBoolean, this::storeBoolean);
    setStorageListener(property);
    return property;
  }

  default StorageProperty<Double> doubleProperty(String key, Double defaultValue) {
    StorageProperty<Double> property =
        new StorageProperty<>(key, defaultValue, this::loadDouble, this::storeDouble);
    setStorageListener(property);
    return property;
  }

  default StorageProperty<Long> longProperty(String key, Long defaultValue) {
    StorageProperty<Long> property =
        new StorageProperty<>(key, defaultValue, this::loadLong, this::storeLong);
    setStorageListener(property);
    return property;
  }

  default StorageProperty<List<String>> listProperty(String key, List<String> defaultValue) {
    StorageProperty<List<String>> property =
        new StorageProperty<>(key, defaultValue, this::loadList, this::storeList);
    setStorageListener(property);
    return property;
  }

}
