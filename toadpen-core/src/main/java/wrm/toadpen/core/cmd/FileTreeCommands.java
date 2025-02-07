package wrm.toadpen.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.inject.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serial;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import wrm.toadpen.core.cmd.CommandManager.Command;
import wrm.toadpen.core.cmd.CommandManager.CommandNoArg;
import wrm.toadpen.core.os.OsNativeService;
import wrm.toadpen.core.storage.SessionProperties;
import wrm.toadpen.core.ui.MainWindow;
import wrm.toadpen.core.ui.dialogs.SearchBox;
import wrm.toadpen.core.ui.dialogs.SimpleDialogs;
import wrm.toadpen.core.ui.editor.EditorComponent;
import wrm.toadpen.core.ui.editor.EditorFactory;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.watchdog.FileWatchDog;

@Factory
@Log
public class FileTreeCommands {

  public static final String FILETREE_NEWFILE = "filetree.newfile";
  public static final String FILETREE_NEWDIR = "filetree.newdir";
  public static final String FILETREE_RENAME = "filetree.rename";
  public static final String FILETREE_DELETE = "filetree.delete";


  @Inject
  CommandManager commandManager;

  @Inject
  MainWindow mainWindow;

  @Inject
  FileTree fileTree;

  @Inject
  EditorFactory editorFactory;

  @Inject
  SessionProperties sessionProperties;

  @Inject
  FileWatchDog fileWatchDog;


  @RequiredArgsConstructor
  public static class FileTreeNewFileCommand implements Command {
    private final Path parentDirectory;
  }

  @RequiredArgsConstructor
  public static class FileTreeNewDirCommand implements Command {
      private final Path parentDirectory;
  }

  @RequiredArgsConstructor
  public static class FileTreeRenameCommand implements Command {
      private final Path file;
  }

  @RequiredArgsConstructor
  public static class FileTreeDeleteCommand implements Command {
      private final File[] file;
  }

  @PostConstruct
  void init() {
    commandManager.registerCommandExecutor(FileTreeNewFileCommand.class, this::createNewFile);
    commandManager.registerCommandExecutor(FileTreeNewDirCommand.class, this::createNewDir);
    commandManager.registerCommandExecutor(FileTreeRenameCommand.class, this::renameFile);
    commandManager.registerCommandExecutor(FileTreeDeleteCommand.class, this::deleteFile);

  }

  @SneakyThrows
  void createNewFile(FileTreeNewFileCommand cmd) {
    String newFileName = SimpleDialogs.inputDialog("Enter file name", "New File", null);
    if (newFileName == null) {
      return;
    }
    File file = cmd.parentDirectory.resolve(newFileName).toFile();
    file.getParentFile().mkdirs();
    file.createNewFile();
    fileTree.refreshTree(file);
    SwingUtilities.invokeLater(() -> fileTree.selectFile(file));
  }

  private void createNewDir(FileTreeNewDirCommand fileTreeNewDirCommand) {
    String newDirName = SimpleDialogs.inputDialog("Enter directory name", "New Directory", null);
    if (newDirName == null) {
      return;
    }
    File dir = fileTreeNewDirCommand.parentDirectory.resolve(newDirName).toFile();
    dir.mkdirs();
    fileTree.refreshTree(dir);
    SwingUtilities.invokeLater(() -> fileTree.selectFile(dir));
  }

  @SneakyThrows
  private void deleteFile(FileTreeDeleteCommand fileTreeDeleteCommand) {
    String quantify = fileTreeDeleteCommand.file.length <= 1
        ? "this file"
        : (fileTreeDeleteCommand.file.length + " files");
    if (SimpleDialogs.confirmationYesNo("Delete File", "Do you really want to delete " + quantify + "?")
        != SimpleDialogs.DialogResult.YES) {
      return;
    }

    for (File path : fileTreeDeleteCommand.file) {
      try {
        File file = path;
        if (file.isDirectory()) {
          FileUtils.deleteDirectory(file);
        } else {
          file.delete();
        }
        fileTree.refreshTree(file);
      } catch (Exception e) {
        log.warning("Error deleting file: " + e.getMessage());
      }
    }
  }

  private void renameFile(FileTreeRenameCommand fileTreeRenameCommand) {
    File file = fileTreeRenameCommand.file.toFile();
    String newName = SimpleDialogs.inputDialog("Enter new name", "Rename File", file.getName());
    if (newName == null) {
      return;
    }
    File newFile = file.getParentFile().toPath().resolve(newName).toFile();
    file.renameTo(newFile);
    fileTree.refreshTree(file.getParentFile());
    SwingUtilities.invokeLater(() -> fileTree.selectFile(newFile));
  }
}
