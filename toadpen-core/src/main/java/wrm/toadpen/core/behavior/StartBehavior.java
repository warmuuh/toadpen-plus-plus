package wrm.toadpen.core.behavior;

import io.avaje.inject.External;
import io.avaje.inject.events.Event;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.File;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.core.CommandlineArgs;
import wrm.toadpen.core.cmd.CommandManager;
import wrm.toadpen.core.cmd.FileCommands;
import wrm.toadpen.core.os.OsNativeService;

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
      initialize(commandlineArgs.getFile());
      OsNativeService.INSTANCE.noteNewRecentDocumentURL(commandlineArgs.getFile());
    }
  }

  public void initialize(File file) {
    if (file.isDirectory()) {
      onApplicationStarted.fire(
          new ApplicationStartedEvent(null, file));
    } else {
      CommandManager.Command command =
          new FileCommands.OpenFileCommand(file);
      commandManager.executeCommand(command);
      onApplicationStarted.fire(
          new ApplicationStartedEvent(file, null));
    }
  }
}
