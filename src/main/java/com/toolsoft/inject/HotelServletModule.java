package com.toolsoft.inject;

import com.google.inject.servlet.ServletModule;
import com.toolsoft.api.HotelAttractions;
import com.toolsoft.api.HotelInfo;
import com.toolsoft.api.HotelLike;
import com.toolsoft.api.HotelReviews;
import com.toolsoft.api.Login;
import com.toolsoft.client.TouristAttractionFinder;
import com.toolsoft.dao.HotelDao;
import com.toolsoft.dao.HotelLikeDao;
import com.toolsoft.dao.LoginDao;
import com.toolsoft.dao.ReviewDao;

/**
 * Defines the dependency injection for the servlets. The url are map to a certain servlet that will
 * dispatch such request.
 */
public final class HotelServletModule extends ServletModule {

  @Override
  protected void configureServlets() {
    requireBinding(HotelDao.class);
    requireBinding(HotelLikeDao.class);
    requireBinding(LoginDao.class);
    requireBinding(ReviewDao.class);
    requireBinding(TouristAttractionFinder.class);
    serve("/attractions").with(HotelAttractions.class);
    serve("/hotelInfo").with(HotelInfo.class);
    serve("/hotelLike").with(HotelLike.class);
    serve("/login").with(Login.class);
    serve("/reviews").with(HotelReviews.class);
  }
}
