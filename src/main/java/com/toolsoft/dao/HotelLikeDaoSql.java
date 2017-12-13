package com.toolsoft.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.toolsoft.dao.DaoUtil.toHotel;

import com.google.common.collect.ImmutableList;
import com.toolsoft.model.Hotel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * Data Access Object to work with {@link Hotel} instances that have been liked capabilities using a
 * RDBMS.
 */
public final class HotelLikeDaoSql implements HotelLikeDao {

  private static final Logger log = Logger.getLogger(HotelLikeDaoSql.class.getName());
  private static final String FIND_USER = "(SELECT userid FROM users WHERE username = ?)";
  private static final String INSERT_SQL =
      "INSERT INTO SavedHotels (users_userid, Hotels_hotelId) VALUES (%s, ?);";
  private static final String STORE_SQL = String.format(INSERT_SQL, FIND_USER);
  private static final String GET_SQL =
      "SELECT h.*, AVG(r.rating) as rating "
          + "FROM SavedHotels sh INNER JOIN Hotels h ON (sh.Hotels_hotelId = h.hotelId) "
          + "INNER JOIN Reviews r ON (h.hotelId = r.hotelId) "
          + "INNER JOIN users u ON (sh.users_userid = u.userid) "
          + "WHERE u.username = ? GROUP BY h.hotelId";
  private static final String DELETE_SQL = "DELETE FROM SavedHotels WHERE users_userid = %s";
  private static final String REMOVE_SQL = String.format(DELETE_SQL, FIND_USER);
  private final Connection connection;

  @Inject
  public HotelLikeDaoSql(Connection connection) {
    this.connection = checkNotNull(connection);
  }

  @Override
  public void store(int hotelId, String user) {
    try (PreparedStatement statement = connection.prepareStatement(STORE_SQL);) {
      statement.setString(1, user);
      statement.setInt(2, hotelId);
      statement.executeUpdate();
    } catch (SQLException e) {
      log.severe(e.getMessage());
      throw new IllegalStateException(
          String.format("Unable to store the hotel\n%s.", e.getMessage()), e);
    }
  }

  @Override
  public int remove(String user) {
    try (PreparedStatement statement = connection.prepareStatement(REMOVE_SQL);) {
      statement.setString(1, user);
      return statement.executeUpdate();
    } catch (SQLException e) {
      log.severe(e.getMessage());
      throw new IllegalStateException(
          String.format("Unable to remove the hotels\n%s.", e.getMessage()), e);
    }
  }

  @Override
  public List<Hotel> get(String user) {
    ImmutableList.Builder<Hotel> hotels = ImmutableList.builder();
    try (PreparedStatement ps = connection.prepareStatement(GET_SQL)) {
      ps.setString(1, user);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        hotels.add(toHotel(rs, true));
      }
    } catch (SQLException e) {
      throw new IllegalStateException("SQL error", e);
    }
    return hotels.build();
  }
}
