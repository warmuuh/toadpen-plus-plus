package wrm.asd.core;

import java.io.File;
import lombok.Value;

@Value
public class CommandlineArgs {

  File file;

  public static CommandlineArgs of(String[] args) {
    if (args.length == 0) {
      return new CommandlineArgs(null);
    } else {
      File file = new File(args[0]);
      return new CommandlineArgs(file);
    }
  }
}
