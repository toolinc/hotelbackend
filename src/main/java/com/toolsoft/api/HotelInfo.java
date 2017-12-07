package com.toolsoft.api;

import java.io.IOException;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class HotelInfo extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    response.setContentType("application/json");
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.getWriter().println("[{\n"
        + "\"success\":true,\n"
        + "\"hotelId\":\"1\",\n"
        + "\"name\":\"Hilton San Francisco Union Square\",\n"
        + "\"rating\":\"2.45\",\n"
        + "\"addr\":\"333 O'Farrell St.\",\n"
        + "\"city\":\"San Francisco\",\n"
        + "\"state\":\"CA\",\n"
        + "\"lat\":\"37.78616\",\n"
        + "\"lng\":\"-122.41018\"\n"
        + "},{\n"
        + "\"success\":true,\n"
        + "\"hotelId\":\"2\",\n"
        + "\"name\":\"Parc 55 San Francisco - A Hilton Hotel\",\n"
        + "\"rating\":\"3.36\",\n"
        + "\"addr\":\"55 Cyril Magnin St\",\n"
        + "\"city\":\"San Francisco\",\n"
        + "\"state\":\"CA\",\n"
        + "\"lat\":\"37.78458\",\n"
        + "\"lng\":\"-122.40854\"\n"
        + "},{\n"
        + "\"success\":true,\n"
        + "\"hotelId\":\"3\",\n"
        + "\"name\":\"Travelodge San Francisco Airport-North\",\n"
        + "\"rating\":\"3.32\",\n"
        + "\"addr\":\"326 S Airport Blvd\",\n"
        + "\"city\":\"South San Francisco\",\n"
        + "\"state\":\"CA\",\n"
        + "\"lat\":\"37.645129\",\n"
        + "\"lng\":\"-122.40492\"\n"
        + "}]");
  }
}
