package wrm.toadpen.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.inject.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.Value;
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
import wrm.toadpen.core.ui.editor.EditorComponent;
import wrm.toadpen.core.ui.editor.EditorFactory;
import wrm.toadpen.core.ui.filetree.FileTree;
import wrm.toadpen.core.watchdog.FileWatchDog;

@Factory
public class FileCommands {

  public static final String FILE_NEW = "file.new";
  public static final String FILE_OPEN = "file.open";
  public static final String FILE_SAVE = "file.save";
  public static final String FILE_FIND = "file.find";
  public static final String FILE_RECENT = "file.recent";

  @Value
  @AllArgsConstructor
  public static class OpenFileCommand implements Command {
    File file;
    int line;
    int column;

    public OpenFileCommand(File file) {
      this.file = file;
      this.line = 1;
      this.column = 1;
    }
  }

  @Value
  public static class SaveEditorFileCommand implements Command {
    EditorComponent editorComponent;
  }

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

  @Bean
  @Named(FILE_NEW)
  CommandManager.CommandNoArg createNewFileCommand() {
    return new CommandNoArg(FILE_NEW, "Create a new File", "/icons/new_file.png",
        this::createNewFile);
  }

  @Bean
  @Named(FILE_OPEN)
  CommandManager.CommandNoArg createOpenFileCommand() {
    return new CommandManager.CommandNoArg(FILE_OPEN, "Open a File", "/icons/open_file.png",
        this::openFile);
  }

  @Bean
  @Named(FILE_SAVE)
  CommandManager.CommandNoArg saveFileCommand() {
    return new CommandNoArg(FILE_SAVE, "Save a File", "/icons/save_file.png", this::saveFile);
  }

  @Bean
  @Named(FILE_FIND)
  CommandManager.CommandNoArg findFileCommand() {
    return new CommandNoArg(FILE_FIND, "Find a file", "/icons/find_file.png", this::findFile);
  }

  @Bean
  @Named(FILE_RECENT)
  CommandManager.CommandNoArg recentFileCommand() {
    return new CommandNoArg(FILE_RECENT, "Open a recent file", "/icons/recent_file.png",
        this::openRecentFile);
  }

  @PostConstruct
  void init() {
    commandManager.registerCommandExecutor(OpenFileCommand.class, this::openFileCommand);
    commandManager.registerCommandExecutor(SaveEditorFileCommand.class, this::saveFileCommand);
  }

  void createNewFile() {
    EditorComponent editor = editorFactory.createEditor();
    mainWindow.addNewEditor(editor);
  }

  private void openFileCommand(OpenFileCommand cmd) {
    openFile(cmd.getFile(), cmd.getLine(), cmd.getColumn());
  }

  @SneakyThrows
  void openFile() {
    File file = OsNativeService.INSTANCE.openFileDialog();
    if (file == null) {
      return;
    }
    openFile(file, 1, 1);
    OsNativeService.INSTANCE.noteNewRecentDocumentURL(file);
  }

  @SneakyThrows
  private void openFile(File selectedFile, int line, int column) {
    sessionProperties.addRecentFile(selectedFile);
    if (selectedFile.exists()) {
      EditorComponent editorComponent = mainWindow.showEditorIfAlreadyOpened(selectedFile);
      if (editorComponent != null) {
        SwingUtilities.invokeLater(() -> editorComponent.goToLine(line, column));
        return;
      }
      String text =
          IOUtils.toString(selectedFile.toURI(), Charset.defaultCharset());
      EditorComponent editor = editorFactory.createEditor(selectedFile, text);
      SwingUtilities.invokeLater(() -> editor.goToLine(line, column));
      fileWatchDog.watch(selectedFile, () -> triggerFileReload(editor));
      mainWindow.addNewEditor(editor);
    } else {
      EditorComponent editor = editorFactory.createEditor(selectedFile, "");
      editor.setDirtyState(true);
      mainWindow.addNewEditor(editor);
    }
  }

  @SneakyThrows
  void saveFile() {

    EditorComponent activeEditor = mainWindow.getActiveEditor();
    if (activeEditor == null) {
      return;
    }

    saveFile(activeEditor);
  }

  @SneakyThrows
  void saveFileCommand(SaveEditorFileCommand cmd) {
    saveFile(cmd.getEditorComponent());
  }

  private void saveFile(EditorComponent activeEditor) throws IOException {
    File file = activeEditor.getFile();
    if (file != null) {
      fileWatchDog.pauseWatch(activeEditor.getFile(), 1000);
    } else {
      file = OsNativeService.INSTANCE.saveFileDialog();
      if (file == null) {
        return;
      }
    }


    IOUtils.write(activeEditor.getText().getBytes(StandardCharsets.UTF_8),
        new FileOutputStream(file));
    fileWatchDog.rehashIfWatched(activeEditor.getFile());

    activeEditor.setDirtyState(false);
  }


  @SneakyThrows
  void findFile() {
    File root = fileTree.getRoot();

    List<File> files = new LinkedList<>();
    FileUtils.iterateFiles(root,
            CanReadFileFilter.CAN_READ,
            new AndFileFilter(HiddenFileFilter.VISIBLE, new NameFileFilter(".git").negate())
        )
        .forEachRemaining(
            f -> files.add(root.toPath().relativize(f.toPath()).toFile())
        );
    if (!files.isEmpty()) {
      SearchBox<File> searchBox =
          new SearchBox<>("Find File...", files);
      File selectedFile = searchBox.showDialog();
      if (selectedFile != null) {
        openFile(new File(root, selectedFile.getPath()), 1, 1);
      }
    }
  }

  @SneakyThrows
  void openRecentFile() {
    File root = fileTree.getRoot();

    List<File> files = sessionProperties.getRecentFiles().stream().map(absPath -> {
      Path path = root.toPath().relativize(new File(absPath).toPath());
      return path.toFile();
    }).toList();

    if (!files.isEmpty()) {
      SearchBox<File> searchBox =
          new SearchBox<>("Open Recent File...", files);
      File selectedFile = searchBox.showDialog();
      if (selectedFile != null) {
        openFile(new File(root, selectedFile.getPath()), 1, 1);
      }
    }
  }

  public void triggerFileReload(EditorComponent editor) {
    mainWindow.showEditorIfAlreadyOpened(editor.getFile());

    if (editor.getFile() != null && editor.getFile().exists()) {
      int value = JOptionPane.showOptionDialog(mainWindow.getFrame(),
          "The file " + editor.getFilename() + " has changed on disk. Do you want to reload it?",
          "File changed", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
          new String[] {"Yes", "No"}, "Yes");
      if (value == JOptionPane.YES_OPTION) {
        reloadFile(editor);
      }
    }
    if (editor.getFile() != null && !editor.getFile().exists()) {
      int value = JOptionPane.showOptionDialog(mainWindow.getFrame(),
          "The file " + editor.getFilename() + " has been deleted. Do you want to close it?",
          "File deleted", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
          new String[] {"Yes", "No"}, "Yes");
      if (value == JOptionPane.YES_OPTION) {
        mainWindow.closeEditor(editor);
      }
    }


  }

  private void reloadFile(EditorComponent editor) {
    try {
      String text = IOUtils.toString(editor.getFile().toURI(), Charset.defaultCharset());
      editor.getTextArea().setText(text);
      editor.setDirtyState(false);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
