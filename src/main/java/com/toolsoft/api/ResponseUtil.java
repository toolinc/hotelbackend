package com.toolsoft.api;

import com.toolsoft.model.Address;
import com.toolsoft.model.Hotel;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

final class ResponseUtil {

  private static final String JSON = "application/json";
  private static final String CORS = "Access-Control-Allow-Origin";
  private static final String CORS_VALUE = "*";

  private ResponseUtil() {}

  static final void setJsonContentType(HttpServletResponse response) {
    response.setContentType(JSON);
  }

  /*
  * Cross-Origin Resource Sharing (CORS)
   */
  static final void setCors(HttpServletResponse response) {
    response.setHeader(CORS, CORS_VALUE);
  }

  static final String hotelsToJson(List<Hotel> hotels) {
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
