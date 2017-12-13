package com.toolsoft.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.api.ResponseUtil.hotelsToJson;
import static com.toolsoft.api.ResponseUtil.setCors;
import static com.toolsoft.api.ResponseUtil.setJsonContentType;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.toolsoft.dao.HotelDao;
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
    List<Hotel> hotels = hotelDao.getHotelsRatingByCity(city);
    setCors(response);
    setJsonContentType(response);
    if (Strings.isNullOrEmpty(version)) {
      response.getWriter().print(hotelsToJson(hotels));
    } else {
      response.getWriter().print(new Gson().toJson(hotels));
    }
  }
}
