package com.toolsoft.inject;

import com.google.inject.servlet.ServletModule;
import com.toolsoft.api.HotelInfo;
import com.toolsoft.api.Login;
import com.toolsoft.dao.HotelDao;

/**
 * Defines the dependency injection for the servlets. The url are map to a certain servlet that will
 * dispatch such request.
 */
public final class HotelServletModule extends ServletModule {

  @Override
  protected void configureServlets() {
    requireBinding(HotelDao.class);
    serve("/hotelInfo").with(HotelInfo.class);
    serve("/login").with(Login.class);
  }
}
