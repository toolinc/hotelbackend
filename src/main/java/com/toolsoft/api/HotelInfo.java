package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.toolsoft.dao.HotelDao;
import com.toolsoft.model.Address;
import com.toolsoft.model.Hotel;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;

@Singleton
public final class HotelInfo extends HttpServlet {

  private static final String JSON = "application/json";
  private static final String CITY = "city";
  private static final String API = "api";
  private final HotelDao hotelDao;

  @Inject
  public HotelInfo(HotelDao hotelDao) {
    this.hotelDao = checkNotNull(hotelDao);
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    String city = city = StringEscapeUtils.escapeHtml4(request.getParameter(CITY));
    String version = request.getParameter(API);
    response.setContentType(JSON);
    response.setHeader("Access-Control-Allow-Origin", "*");
    List<Hotel> hotels = hotelDao.getHotelsRatingByCity(city);
    if (Strings.isNullOrEmpty(version)) {
      response.getWriter().print(oldJson(hotels));
    } else {
      response.getWriter().print(new Gson().toJson(hotels));
    }
  }

  public String oldJson(List<Hotel> hotels) {
    StringBuilder sb = new StringBuilder("[");
    for (Hotel hotel : hotels) {
      sb.append("{" + System.lineSeparator() + "\"success\":");
      Address address = hotel.address();
      sb.append("true," + System.lineSeparator());
      sb.append("\"hotelId\":\"" + hotel.id() + "\"," + System.lineSeparator());
      sb.append("\"name\":\"" + hotel.name() + "\"," + System.lineSeparator());
      sb.append("\"image\":\"" + hotel.image() + "\"," + System.lineSeparator());
      sb.append("\"link\":\"" + hotel.link() + "\"," + System.lineSeparator());
      sb.append("\"rating\":\"" + hotel.rating() + "\"," + System.lineSeparator());
      sb.append("\"addr\":\"" + address.streetAddress() + "\"," + System.lineSeparator());
      sb.append("\"city\":\"" + address.city() + "\"," + System.lineSeparator());
      sb.append("\"state\":\"" + address.state() + "\"," + System.lineSeparator());
      sb.append("\"lat\":\"" + address.lat() + "\"," + System.lineSeparator());
      sb.append("\"lng\":\"" + address.lon() + "\"" + System.lineSeparator());
      sb.append("},");
    }
    if (sb.length() > 1) {
      sb.deleteCharAt(sb.lastIndexOf(","));
    }
    return sb.append("]").toString();
  }
}
