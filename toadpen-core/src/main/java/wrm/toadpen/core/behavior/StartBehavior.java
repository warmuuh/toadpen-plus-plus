package wrm.toadpen.core.behavior;

import io.avaje.inject.External;
import io.avaje.inject.events.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.core.CommandlineArgs;
import wrm.toadpen.core.cmd.CommandManager;
import wrm.toadpen.core.cmd.FileCommands;

@Singleton
@RequiredArgsConstructor
public class StartBehavior {

  @External
  @Inject
  CommandlineArgs commandlineArgs;

  @Inject
  FileCommands fileCommands;

  @Inject
  CommandManager commandManager;

  @Inject
  Event<ApplicationStartedEvent> onApplicationStarted;

  public void initialize() {
    if (commandlineArgs.getFile() != null) {
      if (commandlineArgs.getFile().isDirectory()) {
        onApplicationStarted.fire(
            new ApplicationStartedEvent(null, commandlineArgs.getFile()));
      } else {
        CommandManager.Command command =
            new FileCommands.OpenFileCommand(commandlineArgs.getFile());
        commandManager.executeCommand(command);
        onApplicationStarted.fire(
            new ApplicationStartedEvent(commandlineArgs.getFile(), null));
      }
    }
  }
}
