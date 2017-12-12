package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.api.ResponseUtil.setCors;
import static com.toolsoft.api.ResponseUtil.setJsonContentType;

import com.toolsoft.client.TouristAttractionFinder;
import com.toolsoft.dao.HotelDao;
import com.toolsoft.model.Hotel;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public final class HotelAttractions extends HttpServlet {

  private static final String ID = "hotelId";
  private static final String RADIUS = "radius";
  private final HotelDao hotelDao;
  private final TouristAttractionFinder attractionFinder;

  @Inject
  public HotelAttractions(HotelDao hotelDao, TouristAttractionFinder attractionFinder) {
    this.hotelDao = checkNotNull(hotelDao);
    this.attractionFinder = checkNotNull(attractionFinder);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    Optional<String> id = Optional.ofNullable(request.getParameter(ID));
    Optional<String> radius = Optional.ofNullable(request.getParameter(RADIUS));
    Optional<Hotel> hotel = Optional.empty();
    if (id.isPresent()) {
      hotel = hotelDao.getHotel(id.get());
    }
    setCors(response);
    setJsonContentType(response);
    String json = null;
    if (hotel.isPresent() && id.isPresent() && radius.isPresent()) {
      json = attractionFinder.getHotelAttractions(hotel.get(), Integer.valueOf(radius.get()));
    } else {
      json = buildDefaultResponse(id);
    }
    response.getWriter().print(json);
  }

  private String buildDefaultResponse(Optional<String> id) {
    StringBuilder sb = new StringBuilder("{" + System.lineSeparator() + "\"success\":")
        .append("false," + System.lineSeparator());
    if (!id.isPresent()) {
      sb.append("\"hotelId\":");
    } else {
      sb.append("\"radius\":");
    }
    return sb.append("\"invalid\"" + System.lineSeparator())
        .append("}").toString();
  }
}
