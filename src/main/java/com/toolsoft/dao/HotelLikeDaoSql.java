package com.toolsoft.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.toolsoft.model.Hotel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * Data Access Object to work with {@link Hotel} instances that have been liked capabilities using a
 * RDBMS.
 */
public final class HotelLikeDaoSql implements HotelLikeDao {

  private static final Logger log = Logger.getLogger(HotelLikeDaoSql.class.getName());
  private static final String FIND_USER = "SELECT userid FROM users WHERE username = ?";
  private static final String INSERT_SQL =
      "INSERT INTO SavedHotels (users_userid, Hotels_hotelId) VALUES (%s, ?);";
  private static final String STORE_SQL = String.format(INSERT_SQL, FIND_USER);
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
      throw new IllegalStateException("Unable to store the hotel.", e);
    }
  }
}
