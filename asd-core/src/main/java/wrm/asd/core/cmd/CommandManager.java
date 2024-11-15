package wrm.asd.core.cmd;

import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.jspecify.annotations.Nullable;

@Singleton
@Log
public class CommandManager {

  private Map<String, CommandHandlerNoArg> registeredCommands = new HashMap<>();

  public CommandManager(List<CommandHandlerNoArg> commands) {
    commands.forEach(c -> registeredCommands.put(c.id(), c));
  }

  public @Nullable CommandHandlerNoArg getCommand(String id) {
    return registeredCommands.get(id);
  }

  public void executeCommandById(String id) {
    CommandHandlerNoArg command = registeredCommands.get(id);
    if (command != null) {
      executeCommand(command);
    } else {
      log.severe("Failed to execute command with id: " + id);
    }
  }

  public void executeCommand(CommandHandler command) {
    command.execute();
  }

  public interface CommandHandler {
    void execute();
  }

  @Value
  @Accessors(fluent = true)
  public static class CommandHandlerNoArg implements CommandHandler {
    String id;
    String description;
    @Nullable String icon;

    @Getter(AccessLevel.NONE)
    Runnable executable;

    public void execute() {
      executable.run();
    }
  }

  @RequiredArgsConstructor
  public static class CommandHandlerSingleArg<T> implements CommandHandler {
    private final T arg;
    private final Consumer<T> executable;

    public void execute() {
      executable.accept(arg);
    }
  }

}
