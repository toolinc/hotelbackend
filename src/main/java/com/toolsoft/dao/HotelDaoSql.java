package com.toolsoft.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.toolsoft.model.Address;
import com.toolsoft.model.Hotel;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Data Access Object to work with {@link Hotel} instances in a RDBMS.
 */
public final class HotelDaoSql implements HotelDao {

  private static final String ALL_SQL = "SELECT * FROM Hotels";
  private static final String ID_SQL = "SELECT * FROM Hotels WHERE hotelId= ?";
  private final DataSource dataSource;

  @Inject
  HotelDaoSql(DataSource dataSource) {
    this.dataSource = checkNotNull(dataSource);
  }

  @Override
  public List<Hotel> getAll() {
    ImmutableList.Builder<Hotel> hotels = ImmutableList.builder();
    try (ResultSet rs = dataSource.getConnection().prepareStatement(ALL_SQL).executeQuery()) {
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
    try (PreparedStatement ps = dataSource.getConnection().prepareStatement(ID_SQL)) {
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
    Address address = Address.builder()
        .setStreetAddress(rs.getString("streetAddress"))
        .setCity(rs.getString("city"))
        .setState(rs.getString("state"))
        .setLat(rs.getDouble("lat"))
        .setLon(rs.getDouble("lon"))
        .build();
    return Hotel
        .builder()
        .setId(rs.getString("hotelId"))
        .setName(rs.getString("name"))
        .setAddress(address)
        .build();
  }
}
