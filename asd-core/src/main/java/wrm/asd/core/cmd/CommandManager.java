package wrm.asd.core.cmd;

import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.jspecify.annotations.Nullable;

@Singleton
@Log
public class CommandManager {

  private Map<String, CommandNoArg> registeredCommands = new HashMap<>();
  private Map<Class<? extends Command>, CommandExecutor<?>> commandExecutors = new HashMap<>();

  public CommandManager(List<CommandNoArg> commands) {
    commands.forEach(c -> registeredCommands.put(c.id(), c));
  }


  public Collection<CommandNoArg> getAllNoArgsCommands() {
    return registeredCommands.values();
  }

  public CommandManager.@Nullable CommandNoArg getCommand(String id) {
    return registeredCommands.get(id);
  }

  public <T extends Command> void registerCommandExecutor(Class<T> commandClass,
                                                          CommandExecutor<T> executor) {
    commandExecutors.put(commandClass, executor);
  }

  public void executeCommandById(String id) {
    CommandNoArg command = registeredCommands.get(id);
    if (command != null) {
      executeCommand(command);
    } else {
      log.severe("Failed to execute command with id: " + id);
    }
  }

  public void executeCommand(Command command) {
    if (command instanceof CommandNoArg cmd) {
      cmd.execute();
    } else {
      CommandExecutor commandExecutor = commandExecutors.get(command.getClass());
      if (commandExecutor == null) {
        log.severe("No executor found for command: " + command);
      } else {
        commandExecutor.execute(command);
      }
    }
  }

  public interface Command {
  }

  @FunctionalInterface
  public interface CommandExecutor<T> {
    void execute(T command);
  }

  @Value
  @Accessors(fluent = true)
  public static class CommandNoArg implements Command {
    String id;
    String description;
    @Nullable String icon;

    @Getter(AccessLevel.NONE)
    Runnable executable;

    public void execute() {
      executable.run();
    }
  }

}
