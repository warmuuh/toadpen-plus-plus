package wrm.toadpen.core.model;

import io.avaje.inject.Component;
import java.io.File;
import lombok.Getter;
import wrm.toadpen.core.ui.UiEvent1;

@Component
public class ApplicationModel {

  @Getter
  private File projectDirectory;
  public UiEvent1<File> OnProjectDirectoryChanged = new UiEvent1<>();

  public void setProjectDirectory(File projectDirectory) {
    this.projectDirectory = projectDirectory;
    OnProjectDirectoryChanged.fire(projectDirectory);
  }
}
