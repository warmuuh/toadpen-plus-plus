package wrm.toadpen.core.behavior;

import java.io.File;

public record ApplicationStartedEvent(File initialFile, File initialDirectory){
}
