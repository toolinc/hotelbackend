package com.toolsoft.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Specifies the {@link javax.servlet.ServletContextListener} that will be responsible for injecting
 * the proper instances prior a request. The dependency injection framework that will be use is
 * Google Guice.
 */
@WebListener
public class HotelServletContextListener extends GuiceServletContextListener {

  @Override
  protected Injector getInjector() {
    return Guice.createInjector(new HotelServletModule(), new HotelModule());
  }
}
