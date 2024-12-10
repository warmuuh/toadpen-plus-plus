package wrm.toadpen.core.behavior;

import io.avaje.inject.External;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.File;
import lombok.RequiredArgsConstructor;
import wrm.toadpen.core.CommandlineArgs;
import wrm.toadpen.core.cmd.CommandManager;
import wrm.toadpen.core.cmd.FileCommands;
import wrm.toadpen.core.model.ApplicationModel;
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
  ApplicationModel applicationModel;

  public void initialize() {
    if (commandlineArgs.getFile() != null) {
      initialize(commandlineArgs.getFile());
      OsNativeService.INSTANCE.noteNewRecentDocumentURL(commandlineArgs.getFile());
    } else {
      commandManager.executeCommandById(FileCommands.FILE_NEW);
    }
  }

  public void initialize(File file) {
    if (file.isDirectory()) {
      applicationModel.setProjectDirectory(file);
    } else {
      applicationModel.setProjectDirectory(file.getParentFile());
      CommandManager.Command command =
          new FileCommands.OpenFileCommand(file);
      commandManager.executeCommand(command);
    }

  }
}
