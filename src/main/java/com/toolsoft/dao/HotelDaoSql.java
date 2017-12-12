package com.toolsoft.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.toolsoft.model.Address;
import com.toolsoft.model.Hotel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Data Access Object to work with {@link Hotel} instances in a RDBMS.
 */
public final class HotelDaoSql implements HotelDao {

  private static final String ALL_SQL = "SELECT * FROM Hotels";
  private static final String ID_SQL = "SELECT * FROM Hotels WHERE hotelId= ?";
  private static final String HOTELS_RATING_BY_CITY =
      "SELECT h.*, AVG(r.rating) as rating FROM Hotels h NATURAL JOIN Reviews r "
          + "WHERE city = ? GROUP BY h.hotelId";
  private final Connection connection;

  @Inject
  public HotelDaoSql(Connection connection) {
    this.connection = checkNotNull(connection);
  }

  @Override
  public List<Hotel> getHotelsRatingByCity(String city) {
    ImmutableList.Builder<Hotel> hotels = ImmutableList.builder();
    try (PreparedStatement ps = connection.prepareStatement(HOTELS_RATING_BY_CITY)) {
      ps.setString(1, city);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        hotels.add(toHotel(rs, true));
      }
    } catch (SQLException e) {
      throw new IllegalStateException("SQL error", e);
    }
    return hotels.build();
  }

  @Override
  public List<Hotel> getAll() {
    ImmutableList.Builder<Hotel> hotels = ImmutableList.builder();
    try (ResultSet rs = connection.prepareStatement(ALL_SQL).executeQuery()) {
      while (rs.next()) {
        hotels.add(toHotel(rs));
      }
    } catch (SQLException e) {
      throw new IllegalStateException("SQL error", e);
    }
    return hotels.build();
  }

  @Override
  public Optional<Hotel> getHotel(String id) {
    Hotel hotel = null;
    try (PreparedStatement ps = connection.prepareStatement(ID_SQL)) {
      ps.setInt(1, Integer.valueOf(id));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        hotel = toHotel(rs);
      }
    } catch (SQLException e) {
      throw new IllegalStateException("SQL error", e);
    }
    return Optional.ofNullable(hotel);
  }

  private static final Hotel toHotel(ResultSet rs) throws SQLException {
    return toHotel(rs, false);
  }

  private static final Hotel toHotel(ResultSet rs, boolean rating) throws SQLException {
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
