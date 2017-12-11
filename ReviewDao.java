package hotelapp.dao;

import hotelapp.model.Review;

import java.util.TreeSet;

public interface ReviewDao {

    void create(Review review);

    void update(Review review);

    void delete(String reviewId);

    TreeSet<Review> get(int hotelId);
}
