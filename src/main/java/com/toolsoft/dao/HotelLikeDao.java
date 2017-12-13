package com.toolsoft.dao;

import com.toolsoft.model.Hotel;

/**
 * Data Access Object to work with {@link Hotel} instances that have been liked.
 */
public interface HotelLikeDao {

  /**
   * Registers a new hotel as liked for a given user.
   */
  void store(int hotelId, String user);
}
