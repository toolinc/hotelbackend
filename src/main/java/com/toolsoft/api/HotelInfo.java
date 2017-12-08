package com.toolsoft.api;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.toolsoft.model.Address;
import com.toolsoft.model.Hotel;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@Singleton
public class HotelInfo extends HttpServlet {

  @Inject
  private DataSource dataSource;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {
    response.setContentType("application/json");
    response.setHeader("Access-Control-Allow-Origin", "*");

    final String selectSql = "SELECT * FROM Hotels";
    ImmutableList.Builder<Hotel> hotels = ImmutableList.builder();
    PrintWriter out = response.getWriter();
    try (ResultSet rs = dataSource.getConnection().prepareStatement(selectSql).executeQuery()) {
      while (rs.next()) {
        Address address = Address.builder()
            .setStreetAddress(rs.getString("streetAddress"))
            .setCity(rs.getString("city"))
            .setState(rs.getString("state"))
            .setLat(rs.getDouble("lat"))
            .setLon(rs.getDouble("lon"))
            .build();
        hotels.add(
            Hotel
                .builder()
                .setId(rs.getString("hotelId"))
                .setName(rs.getString("name"))
                .setAddress(address)
                .build());
      }
    } catch (SQLException e) {
      throw new ServletException("SQL error", e);
    }

    Gson gson = new Gson();
    out.print(gson.toJson(hotels.build()));
  }
}
