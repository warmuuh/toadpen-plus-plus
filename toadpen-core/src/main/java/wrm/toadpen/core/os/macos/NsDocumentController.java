package wrm.toadpen.core.os.macos;

import static ca.weblite.objc.RuntimeUtils.cls;
import static ca.weblite.objc.RuntimeUtils.clsName;
import static ca.weblite.objc.RuntimeUtils.msg;
import static ca.weblite.objc.RuntimeUtils.msgInt;
import static ca.weblite.objc.RuntimeUtils.msgPointer;
import static ca.weblite.objc.RuntimeUtils.msgString;
import static ca.weblite.objc.RuntimeUtils.sel;
import static ca.weblite.objc.RuntimeUtils.str;

import ca.weblite.objc.NSObject;
import com.sun.jna.Pointer;
import java.io.File;
import java.util.List;

public class NsDocumentController extends NSObject {
  public static final Pointer NULL_POINTER = Pointer.createConstant(0);
  private final Pointer pointer;

  public NsDocumentController(){
    super("NSObject");
    pointer = msgPointer("NSDocumentController", "sharedDocumentController");
    if (pointer.equals(NULL_POINTER)){
      throw new RuntimeException("NSDocumentController is null");
    }
  }

  public void noteNewRecentDocumentURL(File file){
    String fileUrl = "file://" + file.getAbsolutePath();
    Pointer url = msgPointer(cls("NSURL"), sel("URLWithString:"), str(fileUrl));
    msg(pointer, "noteNewRecentDocumentURL:", url);
  }


  public List<File> getRecentDocuments(){
    Pointer array = msgPointer(pointer, "recentDocumentURLs");
    int count = msgInt(array, "count");
    List<File> files = new java.util.ArrayList<>(count);
    for (int i = 0; i < count; i++){
      Pointer url = msgPointer(array, "objectAtIndex:", i);
      Pointer path = msgPointer(url, "path");
      String pathRaw = msgString(path, "UTF8String" );
      files.add(new File(pathRaw));
    }
    return files;
  }

}
