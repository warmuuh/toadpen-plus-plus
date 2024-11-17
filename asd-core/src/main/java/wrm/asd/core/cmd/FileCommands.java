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
import javax.swing.JFileChooser;
import lombok.SneakyThrows;
import lombok.Value;
import org.apache.commons.io.IOUtils;
import wrm.asd.core.cmd.CommandManager.Command;
import wrm.asd.core.cmd.CommandManager.CommandNoArg;
import wrm.asd.core.ui.MainWindow;
import wrm.asd.core.ui.editor.EditorComponent;
import wrm.asd.core.ui.editor.EditorFactory;

@Factory
public class FileCommands {

  public static final String FILE_NEW = "file.new";
  public static final String FILE_OPEN = "file.open";
  public static final String FILE_SAVE = "file.save";

  @Value
  public static class OpenFileCommand implements Command {
    File file;
  }

  @Inject
  CommandManager commandManager;

  @Inject
  MainWindow mainWindow;
  @Inject
  EditorFactory editorFactory;

  @Bean @Named(FILE_NEW)
  CommandManager.CommandNoArg createNewFileCommand() {
    return new CommandNoArg(FILE_NEW, "Create a new File", "/icons/new_file.png", this::createNewFile);
  }

  @Bean @Named(FILE_OPEN)
  CommandManager.CommandNoArg createOpenFileCommand() {
    return new CommandManager.CommandNoArg(FILE_OPEN, "Open a File", "/icons/open_file.png", this::openFile);
  }

  @Bean @Named(FILE_SAVE)
  CommandManager.CommandNoArg saveFileCommand() {
    return new CommandNoArg(FILE_SAVE, "Save a File", "/icons/save_file.png", this::saveFile);
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

    IOUtils.write(activeEditor.getText().getBytes(StandardCharsets.UTF_8), new FileOutputStream(file));
    activeEditor.setDirtyState(false);
  }

}
