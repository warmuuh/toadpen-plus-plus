package wrm.asd.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.avaje.inject.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.StreamSupport;
import javax.swing.JFileChooser;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.file.AccumulatorPathVisitor;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.CanReadFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import wrm.asd.core.cmd.CommandManager.Command;
import wrm.asd.core.cmd.CommandManager.CommandNoArg;
import wrm.asd.core.storage.SessionProperties;
import wrm.asd.core.ui.MainWindow;
import wrm.asd.core.ui.dialogs.SearchBox;
import wrm.asd.core.ui.editor.EditorComponent;
import wrm.asd.core.ui.editor.EditorFactory;
import wrm.asd.core.ui.filetree.FileTree;

@Factory
public class FileCommands {

  public static final String FILE_NEW = "file.new";
  public static final String FILE_OPEN = "file.open";
  public static final String FILE_SAVE = "file.save";
  public static final String FILE_FIND = "file.find";
  public static final String FILE_RECENT = "file.recent";

  @Value
  public static class OpenFileCommand implements Command {
    File file;
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
    return new CommandNoArg(FILE_RECENT, "Open a recent file", "/icons/recent_file.png", this::openRecentFile);
  }

  @PostConstruct
  void init() {
    commandManager.registerCommandExecutor(OpenFileCommand.class, this::openFileCommand);
  }

  void createNewFile() {
    EditorComponent editor = editorFactory.createEditor();
    mainWindow.addNewEditor(editor);
  }

  private void openFileCommand(OpenFileCommand cmd) {
    openFile(cmd.getFile());
  }

  @SneakyThrows
  void openFile() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(MainWindow.getFrame());
    if (result != JFileChooser.APPROVE_OPTION) {
      return;
    }
    File selectedFile = fileChooser.getSelectedFile();
    openFile(selectedFile);
  }

  @SneakyThrows
  private void openFile(File selectedFile) {
    sessionProperties.addRecentFile(selectedFile);
    if (selectedFile.exists()) {
      if (mainWindow.showEditorIfAlreadyOpened(selectedFile)) {
        return;
      }
      String text =
          IOUtils.toString(selectedFile.toURI(), Charset.defaultCharset());
      EditorComponent editor = editorFactory.createEditor(selectedFile, text);
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

    File file = activeEditor.getFile();
    if (file == null) {
      JFileChooser fileChooser = new JFileChooser();
      int result = fileChooser.showSaveDialog(MainWindow.getFrame());
      if (result != JFileChooser.APPROVE_OPTION) {
        return;
      }
      file = fileChooser.getSelectedFile();
    }

    IOUtils.write(activeEditor.getText().getBytes(StandardCharsets.UTF_8),
        new FileOutputStream(file));
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
        openFile(new File(root, selectedFile.getPath()));
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
        openFile(new File(root, selectedFile.getPath()));
      }
    }
  }

}
