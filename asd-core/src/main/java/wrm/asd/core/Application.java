package wrm.asd.core;

import io.avaje.inject.BeanScope;
import wrm.asd.core.cmd.ApplicationController;

public class Application {

  public static void main(String[] args) {
    BeanScope beanscope = BeanScope.builder()
        .bean(CommandlineArgs.class, CommandlineArgs.of(args))
        .build();

    beanscope.get(ApplicationController.class).start();
  }
}
