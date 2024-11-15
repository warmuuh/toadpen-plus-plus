package wrm.asd.core.behavior;

import java.io.File;

public record ApplicationStartedEvent(File initialFile, File initialDirectory){
}
