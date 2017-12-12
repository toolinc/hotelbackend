package com.toolsoft.cache;

import static com.google.common.base.Preconditions.checkNotNull;

import com.toolsoft.model.Address;
import com.toolsoft.model.Review;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class HotelCacheThreadSafe - extends class HotelCache. Thread-safe, uses ReentrantReadWriteLock
 * to synchronize access to all data structures.
 */
@Singleton
public final class HotelCacheThreadSafe extends HotelCache {

  private static final Logger log = Logger.getLogger(HotelCacheThreadSafe.class.getName());
  private final ReentrantReadWriteLock lock;

  @Inject
  public HotelCacheThreadSafe(ReentrantReadWriteLock lock) {
    this.lock = checkNotNull(lock);
  }

  /**
   * Overrides addHotel method from HotelCache class to make it thread-safe; uses the lock. Create a
   * Hotel given the parameters, and add it to the appropriate data structure(s).
   *
   * @param hotelId - the id of the hotel
   * @param hotelName - the name of the hotel
   * @param city - the city where the hotel is located
   * @param state - the state where the hotel is located.
   * @param streetAddress - the building number and the street
   */
  public void addHotel(String hotelId, String hotelName, String city, String state,
      String streetAddress, double lat,
      double lon) {
    try {
      lock.lockWrite();
      super.addHotel(hotelId, hotelName, city, state, streetAddress, lat, lon);
    } finally {
      lock.unlockWrite();
    }
  }

  /**
   * Overrides addReview method from HotelCache class to make it thread-safe; uses the lock.
   *
   * @param hotelId - the id of the hotel reviewed
   * @param reviewId - the id of the review
   * @param rating - integer rating 1-5.
   * @param reviewTitle - the title of the review
   * @param review - text of the review
   * @param isRecom - whether the user recommends it or not
   * @param date - date of the review in the format yyyy-MM-dd, e.g. 2016-08-29.
   * @param username - the nickname of the user writing the review.
   * @return true if successful, false if unsuccessful because of invalid date or rating. Needs to
   * catch and handle the following exceptions: ParseException if the date is invalid
   * InvalidRatingException if the rating is out of range
   */
  public boolean addReview(String hotelId, String reviewId, int rating, String reviewTitle,
      String review,
      boolean isRecom, String date, String username) {
    try {
      lock.lockWrite();
      return super
          .addReview(hotelId, reviewId, rating, reviewTitle, review, isRecom, date, username);
    } finally {
      lock.unlockWrite();
    }

  }

  /**
   * Overrides toString method of class HotelCache to make it thread-safe. Returns a string
   * representing information about the hotel with the given id, including all the reviews for this
   * hotel separated by -------------------- Format of the string: HoteName: hotelId streetAddress
   * city, state -------------------- Review by username: rating ReviewTitle ReviewText
   * -------------------- Review by username: rating ReviewTitle ReviewText ...
   *
   * @return - output string.
   */
  public String toString(String hotelId) {
    try {
      lock.lockRead();
      return super.toString(hotelId);
    } finally {
      lock.unlockRead();
    }
  }

  /**
   * Overrides the method printToFile of the parent class to make it thread-safe. Save the string
   * representation of the hotel data to the file specified by filename in the following format: an
   * empty line A line of 20 asterisks ******************** on the next line information for each
   * hotel, printed in the format described in the toString method of this class. <p> The hotels
   * should be sorted by hotel ids
   *
   * @param filename - Path specifying where to save the output.
   */
  public void printToFile(Path filename) {
    try {
      lock.lockRead();
      super.printToFile(filename);
    } catch (Exception e) {
      log.severe("Exception while running the printToFile ThreadSafe class: " + e);
    } finally {
      lock.unlockRead();
    }
  }

  /**
   * Overrides a method of the parent class to make it thread-safe. Return an alphabetized list of
   * the ids of all hotels
   */
  public List<String> getHotels() {
    try {
      lock.lockRead();
      return super.getHotels();
    } finally {
      lock.unlockRead();
    }
  }


  /**
   * Merge the local Thread information in the main memory
   **/
  public void mergeReviews(HotelCache localHdata) {
    try {
      lock.lockWrite();
      super.mergeReviews(localHdata);
    } finally {
      lock.unlockWrite();
    }
  }

  /**
   * Get the Lat and Lon of the Hotel
   *
   * @Return the location of the Hotel format [lat, lon, city]
   */
  public Object[] getLocationHotel(String hId) {
    try {
      lock.lockRead();
      return super.getLocationHotel(hId);
    } finally {
      lock.unlockRead();
    }
  }

  /**
   * @param hId - Hotel Id
   * @Return the Name of the Hotel
   */
  public String getHotelName(String hId) {
    try {
      lock.lockRead();
      return super.getHotelName(hId);
    } finally {
      lock.unlockRead();
    }
  }

  /**
   * @param hId - Hotel Id
   * @Return the Address of the Hotel
   */
  public Address getAddress(String hId) {
    try {
      lock.lockRead();
      return super.getAddress(hId);
    } finally {
      lock.unlockRead();
    }
  }

  /**
   * @param hId - Hotel Id
   * @Return the Reviews of the Hotel
   */
  public TreeSet<Review> getReviews(String hId) {
    try {
      lock.lockRead();
      return super.getReviews(hId);
    } finally {
      lock.unlockRead();
    }
  }
}
