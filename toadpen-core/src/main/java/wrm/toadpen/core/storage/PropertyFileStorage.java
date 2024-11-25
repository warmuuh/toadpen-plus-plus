package wrm.toadpen.core.storage;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.configuration2.Configuration;

@RequiredArgsConstructor
public class PropertyFileStorage implements Storage {

  private final Configuration config;
  private final SaveCallback saveCallback;
  private final List<StorageListener> listeners = new ArrayList<>();

  @Override
  public void storeInteger(String key, Integer value) {
    listeners.forEach(listener -> listener.onStorageChanged(key, value));
    config.setProperty(key, value);
  }

  @Override
  public Integer loadInteger(String key, Integer defaultValue) {
    return config.getInt(key, defaultValue);
  }

  @Override
  public void storeString(String key, String value) {
    listeners.forEach(listener -> listener.onStorageChanged(key, value));
    config.setProperty(key, value);
  }

  @Override
  public String loadString(String key, String defaultValue) {
    return config.getString(key, defaultValue);
  }

  @Override
  public void storeBoolean(String key, Boolean value) {
    listeners.forEach(listener -> listener.onStorageChanged(key, value));
    config.setProperty(key, value);
  }

  @Override
  public Boolean loadBoolean(String key, Boolean defaultValue) {
    return config.getBoolean(key, defaultValue);
  }

  @Override
  public void storeDouble(String key, Double value) {
    listeners.forEach(listener -> listener.onStorageChanged(key, value));
    config.setProperty(key, value);
  }

  @Override
  public Double loadDouble(String key, Double defaultValue) {
    return config.getDouble(key, defaultValue);
  }

  @Override
  public void storeLong(String key, Long value) {
    listeners.forEach(listener -> listener.onStorageChanged(key, value));
    config.setProperty(key, value);
  }

  @Override
  public Long loadLong(String key, Long defaultValue) {
    return config.getLong(key, defaultValue);
  }

  @Override
  public void storeList(String key, List<String> value) {
    listeners.forEach(listener -> listener.onStorageChanged(key, value));
    config.setProperty(key, value);
  }

  @Override
  public List<String> loadList(String key, List<String> defaultValue) {
    return config.getList(String.class, key, defaultValue);
  }

  @Override
  @SneakyThrows
  public void close() {
    saveCallback.save();
  }

  @Override
  public void setStorageListener(StorageListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeStorageListener(StorageListener listener) {
    listeners.remove(listener);
  }

  @FunctionalInterface
  public interface SaveCallback {
    void save() throws Exception;
  }
}
