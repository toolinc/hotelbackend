package hotelapp.dao;

import hotelapp.DatabaseHelper.DatabaseConnector;
import hotelapp.DatabaseHelper.Status;
import hotelapp.model.InvalidRatingException;
import hotelapp.model.Review;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeSet;

public final class ReviewDaoSql implements ReviewDao {

    private static final String TABLE = "Reviews";

    /**
     * Used to determine if necessary tables are provided.
     */
    private static final String TABLES_SQL =
            "SHOW TABLES LIKE 'reviews';";

    /**
     * Used to create necessary tables for this example.
     */
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE + " ( `reviewId` INT(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `hotelId` INT(11) NOT NULL,\n" +
            "  `rating` double NOT NULL,\n" +
            "  `title` VARCHAR(45) NOT NULL,\n" +
            "  `review` longtext NOT NULL,\n" +
            "  `isRecom` TINYINT(1) DEFAULT NULL,\n" +
            "  `date` VARCHAR(50) NOT NULL,\n" +
            "  `userId` INT(11) NOT NULL,\n" +
            "  PRIMARY KEY (`reviewId`),\n" +
            "  CONSTRAINT `fk_Reviews_Hotels`\n" +
            "    FOREIGN KEY (`hotelId`)\n" +
            "    REFERENCES `HotelAdvisor`.`Hotels` (`hotelId`)\n" +
            "    ON DELETE cascade\n" +
            "    ON UPDATE cascade,\n" +
            "  CONSTRAINT `fk_Reviews_users1`\n" +
            "    FOREIGN KEY (`userId`)\n" +
            "    REFERENCES `HotelAdvisor`.`users` (`userid`)\n" +
            "    ON DELETE cascade\n" +
            "    ON UPDATE cascade)";

    private static final String GET_REVIEW = "SELECT r.*, username FROM users NATURAL JOIN " + TABLE + " r WHERE hotelId = ?";

    private static final String DELET_REVIEW = "DELETE FROM " + TABLE + " WHERE reviewId = ?";

    private static final String CREATE_REVIEW = "INSERT INTO " + TABLE + " (hotelId, rating, title, review, isRecom, date, userId) values (?, ?, ?, ?, ?, ?, " +
            "(SELECT userid FROM users WHERE username = ?));";

    private static final String UPDATE_REVIEW = "UPDATE " + TABLE + " SET title= ?, review=?, isRecom=?, rating=?, date=? WHERE reviewId=?;";

    private DatabaseConnector db;
    private static Logger log = LogManager.getLogger();

    public ReviewDaoSql() {
        Status status = Status.OK;
        try {
            db = new DatabaseConnector("database.properties");
        } catch (FileNotFoundException e) {
            status = Status.MISSING_CONFIG;
        } catch (IOException e) {
            status = Status.MISSING_VALUES;
        }
        if (status != Status.OK) {
            log.fatal(status.message());
        }
    }


    @Override
    public void create(Review review) {
        System.out.println("Create");
        try (
                Connection connection = db.getConnection();
                PreparedStatement statement = connection.prepareStatement(CREATE_REVIEW);
        ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            java.util.Date newDate = new Date();

            statement.setInt(1, Integer.valueOf(review.getReviewId()));
            statement.setInt(2, review.getRating());
            statement.setString(3, review.getReviewTitle());
            statement.setString(4, review.getReview());
            statement.setBoolean(5, review.isRecom());
            statement.setString(6, sdf.format(newDate));
            statement.setString(7, review.getUsername());
            int results = statement.executeUpdate();

            System.out.println(results);

        } catch (SQLException ex) {
            System.out.println("SQLException " + ex);
            log.debug(ex.getMessage(), ex);
        }
    }

    @Override
    public void update(Review review) {
        System.out.println("Update DAO SQL");
        try (
                Connection connection = db.getConnection();
                PreparedStatement statement = connection.prepareStatement(UPDATE_REVIEW);
        ) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            java.util.Date newDate = new Date();
            statement.setString(1, review.getReviewTitle());
            statement.setString(2, review.getReview());
            statement.setBoolean(3, review.isRecom());
            statement.setInt(4, review.getRating());
            statement.setString(5, sdf.format(newDate));
            statement.setInt(6, Integer.valueOf(review.getReviewId()));
            int results = statement.executeUpdate();

            System.out.println(results);

        } catch (SQLException ex) {
            System.out.println("SQLException " + ex);
            log.debug(ex.getMessage(), ex);
        }
    }

    @Override
    public void delete(String reviewId) {
        System.out.println("Delete");
        try (
                Connection connection = db.getConnection();
                PreparedStatement statement = connection.prepareStatement(DELET_REVIEW);
        ) {
            statement.setString(1, reviewId);
            int results = statement.executeUpdate();
            System.out.println(results);

        } catch (SQLException ex) {
            System.out.println("SQLException " + ex);
            log.debug(ex.getMessage(), ex);
        }
    }

    @Override
    public TreeSet<Review> get(int hotelId) {
        TreeSet<Review> reviewTreeSet = new TreeSet<>();

        try (
                Connection connection = db.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_REVIEW);
        ) {
            statement.setInt(1, hotelId);
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                Review review = Review.Builder.newBuilder().setDate(results.getString("date")).setHotelId(results.getString("hotelId"))
                        .setRating((int) results.getDouble("rating")).setReview(results.getString("review"))
                        .setReviewId(results.getString("reviewId")).setReviewTitle(results.getString("title"))
                        .setUsername(results.getString("username")).build();
                reviewTreeSet.add(review);
            }
        } catch (SQLException ex) {
            log.debug(ex.getMessage(), ex);
        } catch (InvalidRatingException | ParseException e) {
            e.printStackTrace();
        }
        return reviewTreeSet;
    }
}
