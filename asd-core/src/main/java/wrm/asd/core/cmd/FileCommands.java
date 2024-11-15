package wrm.asd.core.cmd;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.swing.JFileChooser;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import wrm.asd.core.cmd.CommandManager.CommandHandler;
import wrm.asd.core.cmd.CommandManager.CommandHandlerNoArg;
import wrm.asd.core.cmd.CommandManager.CommandHandlerSingleArg;
import wrm.asd.core.ui.MainWindow;
import wrm.asd.core.ui.editor.EditorComponent;
import wrm.asd.core.ui.editor.EditorFactory;

@Factory
public class FileCommands {

  public static final String FILE_NEW = "file.new";
  public static final String FILE_OPEN = "file.open";
  public static final String FILE_SAVE = "file.save";


  @Inject
  MainWindow mainWindow;
  @Inject
  EditorFactory editorFactory;

  @Bean @Named(FILE_NEW)
  CommandHandlerNoArg createNewFileCommand() {
    return new CommandHandlerNoArg(FILE_NEW, "Create a new File", "/icons/new_file.png", this::createNewFile);
  }

  @Bean @Named(FILE_OPEN)
  CommandHandlerNoArg createOpenFileCommand() {
    return new CommandHandlerNoArg(FILE_OPEN, "Open a File", "/icons/open_file.png", this::openFile);
  }

  @Bean @Named(FILE_SAVE)
  CommandHandlerNoArg saveFileCommand() {
    return new CommandHandlerNoArg(FILE_SAVE, "Save a File", "/icons/save_file.png", this::saveFile);
  }

  public CommandHandler openFileArgCommand(File file) {
    return new CommandHandlerSingleArg<>(file, this::openFile);
  }


  void createNewFile() {
    EditorComponent editor = editorFactory.createEditor();
    mainWindow.addNewEditor(editor);
  }

  @SneakyThrows
  void openFile() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showOpenDialog(mainWindow.getFrame());
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
      int result = fileChooser.showSaveDialog(mainWindow.getFrame());
      if (result != JFileChooser.APPROVE_OPTION) {
        return;
      }
      file = fileChooser.getSelectedFile();
    }

    IOUtils.write(activeEditor.getText().getBytes(StandardCharsets.UTF_8), new FileOutputStream(file));
    activeEditor.setDirtyState(false);
  }

}
