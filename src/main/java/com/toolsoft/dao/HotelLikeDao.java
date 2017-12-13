package com.toolsoft.dao;

import com.toolsoft.model.Hotel;
import java.util.List;

/**
 * Data Access Object to work with {@link Hotel} instances that have been liked.
 */
public interface HotelLikeDao {

  /**
   * Registers a new hotel as liked for a given user.
   */
  void store(int hotelId, String user);

  /**
   * Retrieve a list of hotels liked for a given user.
   */
  List<Hotel> get(String user);
}
