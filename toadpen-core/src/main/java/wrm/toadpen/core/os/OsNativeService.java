package wrm.toadpen.core.os;

import java.io.File;
import java.util.List;
import org.apache.commons.lang3.SystemUtils;
import wrm.toadpen.core.os.macos.MacOsNativeService;

public interface OsNativeService {

  OsNativeService INSTANCE = createInstance();

  static OsNativeService createInstance() {
    if (SystemUtils.IS_OS_MAC) {
      return new MacOsNativeService();
    }
    return new EmulatingNativeService();
  }


  File openFileDialog();

  File saveFileDialog();

  void noteNewRecentDocumentURL(File file);

  List<File> getRecentDocuments();

}
