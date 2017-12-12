package com.toolsoft.dao;

import com.toolsoft.model.Review;
import java.util.TreeSet;

/**
 * Data Access Object to work with {@link Review} instances.
 */
public interface ReviewDao {

  void create(Review review);

  void update(Review review);

  void delete(String reviewId);

  TreeSet<Review> get(int hotelId);
}
