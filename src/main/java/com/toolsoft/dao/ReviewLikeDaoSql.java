package com.toolsoft.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.inject.Inject;

/**
 * Data Access Object to work with {@link com.toolsoft.model.Review} instances that have been voted
 * capabilities using a RDBMS.
 */
public class ReviewLikeDaoSql implements ReviewLikeDao {

  private static final Logger log = Logger.getLogger(ReviewLikeDaoSql.class.getName());
  private static final String FIND_USER = "(SELECT userid FROM users WHERE username = ?)";
  private static final String INSERT_SQL =
      "INSERT INTO LikeReviews (users_userid, reviews_reviewId) VALUES (%s, ?);";
  private static final String STORE_SQL = String.format(INSERT_SQL, FIND_USER);
  private final Connection connection;

  @Inject
  public ReviewLikeDaoSql(Connection connection) {
    this.connection = checkNotNull(connection);
  }

  @Override
  public void store(int reviewId, String user) {
    try (PreparedStatement statement = connection.prepareStatement(STORE_SQL);) {
      statement.setString(1, user);
      statement.setInt(2, reviewId);
      statement.executeUpdate();
    } catch (SQLException e) {
      log.severe(e.getMessage());
      throw new IllegalStateException(
          String.format("Unable to store the review\n%s.", e.getMessage()), e);
    }
  }
}
