package wrm.toadpen.core.log;

import jakarta.inject.Singleton;
import lombok.Value;
import wrm.toadpen.core.ui.UiEvent1;

@Singleton
public class Logger {

  public UiEvent1<LogEvent> OnLogEvent = new UiEvent1<>();

  public enum Level {
    INFO, WARN, ERROR
  }

  @Value
  public class LogEvent {
    String message;
    Exception exception;
    Level level;
  }

  public void error(String message, Exception e) {
    OnLogEvent.fire(new LogEvent(message, e, Level.ERROR));
  }

  public void warn(String message) {
    OnLogEvent.fire(new LogEvent(message, null, Level.WARN));
  }

  public void info(String message) {
    OnLogEvent.fire(new LogEvent(message, null, Level.INFO));
  }

}
