package hotelapp.dao;

import hotelapp.DatabaseHelper.DatabaseConnector;
import hotelapp.DatabaseHelper.Status;
import hotelapp.model.Address;
import hotelapp.model.Hotel;
import hotelapp.model.Hotel.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HotelDaoSql implements HotelDao {

    private static final String HOTEL_TABLE = "Hotels";
    private static final String REVIEWS_TABLE = "Reviews";

    private static final String TABLES_SQL = "SHOW TABLES LIKE 'hotels';";
    private static final String CREATE_TABLE = "CREATE TABLE " + HOTEL_TABLE + "( `hotelId` INT(11) NOT NULL AUTO_INCREMENT,\n" +
            "  `name` VARCHAR(70) NOT NULL,\n" +
            "  `city` VARCHAR(21) NOT NULL,\n" +
            "  `state` CHAR(2) NOT NULL,\n" +
            "  `streetAddress` VARCHAR(100) NOT NULL,\n" +
            "  `lat` DOUBLE NOT NULL,\n" +
            "  `lon` DOUBLE NOT NULL,\n" +
            "  PRIMARY KEY (`hotelId`))";

    private static final String GET_HOTELS =
            "select h.*, AVG(r.rating) as rating from " + HOTEL_TABLE + " h NATURAL JOIN " + REVIEWS_TABLE
                    + " r  WHERE city = ? GROUP BY h.hotelId";
    private static final String HOTEL = "Select * from " + HOTEL_TABLE + " where hotelId= ?";


    private DatabaseConnector db;
    private static Logger log = LogManager.getLogger();

    public HotelDaoSql() {
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

    private void createTable() {

    }

    @Override
    public List<Hotel> getAll(String city) {
        List<Hotel> list = new ArrayList<>();

        try (
                Connection connection = db.getConnection();
                PreparedStatement statement = connection.prepareStatement(GET_HOTELS);
        ) {

            statement.setString(1,city);
            Hotel hotel;
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                Address address = Address.Builder.newBuilder()
                        .setStreetAddress(results.getString("streetAddress"))
                        .setCity(results.getString("city")).setState(results.getString("state"))
                        .setLat(results.getDouble("lat")).setLon(results.getDouble("lon")).build();
                hotel = Builder.newBuilder().setId(results.getString("hotelId"))
                        .setName(results.getString("name"))
                        .setAddress(address).setAverageRating(Math.round(results.getDouble("rating") * 100) / 100.0d).build();
                list.add(hotel);

                System.out.println(results.getString("city"));
            }
        } catch (SQLException ex) {
            log.debug(ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public Hotel getHotel(String id) {
        try (
                Connection connection = db.getConnection();
                PreparedStatement statement = connection.prepareStatement(HOTEL);
        ) {
            Hotel hotel;
            statement.setInt(1, Integer.valueOf(id));
            ResultSet results = statement.executeQuery();

            if (results.next()) {
                System.out.println("result SQL " + results.getString("name"));

                Address address = Address.Builder.newBuilder()
                        .setStreetAddress(results.getString("streetAddress"))
                        .setCity(results.getString("city")).setState(results.getString("state"))
                        .setLat(results.getDouble("lat")).setLon(results.getDouble("lon")).build();
                hotel = Builder.newBuilder().setId(results.getString("hotelId"))
                        .setName(results.getString("name"))
                        .setAddress(address).build();
                System.out.println("Hotel sql " + hotel.getName());
                return hotel;
            }
        } catch (SQLException ex) {
            System.out.println("SQLException " + ex);
            log.debug(ex.getMessage(), ex);
        }
        return null;
    }

}
