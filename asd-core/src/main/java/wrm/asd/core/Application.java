package wrm.asd.core;

import io.avaje.inject.BeanScope;
import wrm.asd.core.cmd.ApplicationController;
import wrm.asd.core.ui.macos.MacosSettings;

public class Application {

  public static void main(String[] args) {

    MacosSettings.init();

    BeanScope beanscope = BeanScope.builder()
        .bean(CommandlineArgs.class, CommandlineArgs.of(args))
        .build();

    beanscope.get(ApplicationController.class).start();
  }
}
