package com.toolsoft.dao;

import com.toolsoft.model.Hotel;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object to work with {@link Hotel} instances.
 */
public interface HotelDao {

  /**
   * Provides all the {@link Hotel} store on the database.
   */
  List<Hotel> getAll();

  /**
   * Provides all the {@link Hotel} in a city stores on the database.
   */
  List<Hotel> getHotelsRatingByCity(String city);

  /**
   * Provides a specific {@link Hotel} from the database.
   */
  Optional<Hotel> getHotel(String id);
}
