package com.toolsoft.dao;

/**
 * Data Access Object to work with {@link com.toolsoft.model.Review} instances that have been liked.
 */
public interface ReviewLikeDao {

  /**
   * Registers a new review as voted for a given user.
   */
  void store(int reviewId, String user);
}
