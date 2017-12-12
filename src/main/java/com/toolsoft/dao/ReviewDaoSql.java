package com.toolsoft.dao;

import static com.google.common.base.Preconditions.checkNotNull;

import com.toolsoft.model.Review;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Data Access Object to work with {@link Review} instances in a RDBMS.
 */
public final class ReviewDaoSql implements ReviewDao {

  private static final String TABLE = "Reviews";
  private static final String GET_REVIEW =
      "SELECT r.*, username FROM users NATURAL JOIN " + TABLE + " r WHERE hotelId = ?";
  private static final String DELET_REVIEW = "DELETE FROM " + TABLE + " WHERE reviewId = ?";
  private static final String CREATE_REVIEW = "INSERT INTO " + TABLE
      + " (hotelId, rating, title, review, isRecom, date, userId) VALUES (?, ?, ?, ?, ?, ?, " +
      "(SELECT userid FROM users WHERE username = ?));";
  private static final String UPDATE_REVIEW =
      "UPDATE " + TABLE + " SET title= ?, review=?, isRecom=?, rating=?, date=? WHERE reviewId=?;";
  private static final Logger log = Logger.getLogger(ReviewDaoSql.class.getName());
  private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private final DataSource dataSource;

  @Inject
  public ReviewDaoSql(DataSource dataSource) {
    this.dataSource = checkNotNull(dataSource);
  }

  @Override
  public void create(Review review) {
    try (
        PreparedStatement statement = dataSource.getConnection().prepareStatement(CREATE_REVIEW);
    ) {
      statement.setInt(1, Integer.valueOf(review.reviewId()));
      statement.setInt(2, review.rating());
      statement.setString(3, review.reviewTitle());
      statement.setString(4, review.review());
      statement.setBoolean(5, review.recom());
      statement.setString(6, SIMPLE_DATE_FORMAT.format(new Date()));
      statement.setString(7, review.username());
      int results = statement.executeUpdate();
      log.info(String.format("Reviews #%d inserted.", results));
    } catch (SQLException ex) {
      log.severe(ex.getMessage());
    }
  }

  @Override
  public void update(Review review) {
    try (
        PreparedStatement statement = dataSource.getConnection().prepareStatement(UPDATE_REVIEW);
    ) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      java.util.Date newDate = new Date();
      statement.setString(1, review.reviewTitle());
      statement.setString(2, review.review());
      statement.setBoolean(3, review.recom());
      statement.setInt(4, review.rating());
      statement.setString(5, sdf.format(newDate));
      statement.setInt(6, Integer.valueOf(review.reviewId()));
      int results = statement.executeUpdate();
      log.info(String.format("Reviews #%d updated.", results));
    } catch (SQLException ex) {
      log.severe(ex.getMessage());
    }
  }

  @Override
  public void delete(String reviewId) {
    System.out.println("Delete");
    try (
        PreparedStatement statement = dataSource.getConnection().prepareStatement(DELET_REVIEW);
    ) {
      statement.setString(1, reviewId);
      int results = statement.executeUpdate();
      log.info(String.format("Reviews #%d deleted.", results));
    } catch (SQLException ex) {
      log.severe(ex.getMessage());
    }
  }

  @Override
  public TreeSet<Review> get(int hotelId) {
    TreeSet<Review> reviewTreeSet = new TreeSet<>();
    try (
        PreparedStatement statement = dataSource.getConnection().prepareStatement(GET_REVIEW);
    ) {
      statement.setInt(1, hotelId);
      ResultSet results = statement.executeQuery();
      while (results.next()) {
        Review review = Review.builder()
            .setHotelId(results.getString("hotelId"))
            .setReviewId(results.getString("reviewId"))
            .setRating((int) results.getDouble("rating"))
            .setReviewTitle(results.getString("title"))
            .setReview(results.getString("review"))
            .setRecom(false)
            .setDate(results.getString("date"))
            .setUsername(results.getString("username"))
            .build();
        reviewTreeSet.add(review);
      }
    } catch (SQLException ex) {
      log.severe(ex.getMessage());
    }
    return reviewTreeSet;
  }
}
