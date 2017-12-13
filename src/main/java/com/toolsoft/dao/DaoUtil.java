package com.toolsoft.dao;

import com.toolsoft.model.Address;
import com.toolsoft.model.Hotel;
import java.sql.ResultSet;
import java.sql.SQLException;

final class DaoUtil {

  private DaoUtil() {
  }

  static final Hotel toHotel(ResultSet rs) throws SQLException {
    return toHotel(rs, false);
  }

  static final Hotel toHotel(ResultSet rs, boolean rating) throws SQLException {
    Address address = Address.builder()
        .setStreetAddress(rs.getString("streetAddress"))
        .setCity(rs.getString("city"))
        .setState(rs.getString("state"))
        .setLat(rs.getDouble("lat"))
        .setLon(rs.getDouble("lon"))
        .build();
    Hotel.Builder hotel = Hotel
        .builder()
        .setId(rs.getString("hotelId"))
        .setName(rs.getString("name"))
        .setAddress(address)
        .setImage(rs.getString("image"))
        .setLink(rs.getString("link"));
    if (rating) {
      hotel.setRating(Math.round(rs.getDouble("rating") * 100) / 100.0d);
    } else {
      hotel.setRating(0);
    }
    return hotel.build();
  }
}
