package com.toolsoft.inject;

import com.google.inject.servlet.ServletModule;
import com.toolsoft.api.HotelInfo;

/**
 * Defines the dependency injection for the back end module.
 */
public class HotelModule extends ServletModule {

  @Override
  protected void configureServlets() {
    serve("/hotelInfo").with(HotelInfo.class);
  }
}
