package wrm.toadpen.core.watchdog;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public enum FileEventType {
  CREATE,
  DELETE,
  MODIFY;

  public static FileEventType fromWatchEventKind(WatchEvent.Kind<?> kind) {
    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
      return CREATE;
    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
      return DELETE;
    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
      return MODIFY;
    } else {
      throw new IllegalArgumentException("Unsupported watch event kind: " + kind);
    }
  }
}
