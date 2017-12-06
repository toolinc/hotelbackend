package com.toolsoft.model;

import com.google.auto.value.AutoValue;
import java.util.Date;

@AutoValue
public abstract class Review implements Comparable<Review> {

  public abstract String hotelId();

  public abstract String reviewId();

  public abstract int rating();

  public abstract String reviewTitle();

  public abstract String review();

  public abstract boolean isRecom();

  public abstract Date date();

  public abstract String username();

  /**
   * Compares this review with the review passed as a parameter based on the dates (more recent date
   * is "less" than older date). If the dates are equal, it compares reviews based on the user
   * nicknames, alphabetically. If user nicknames are the same, it compares based on the review ids.
   * Note that we only care about comparing reviews for the same hotel id.
   *
   * @param other review to compare this one with
   * @return -1 if this review is "less than" the argument, 0 if equal 1 if this review is "greater"
   * than the other one
   */
  @Override
  public int compareTo(Review other) {
    int compare = other.date().compareTo(this.date());
    if (compare == 0) {
      compare = this.username().compareTo(other.username());
      if (compare == 0) {
        return this.reviewId().compareTo(other.reviewId());
      }
      return compare;
    }
    return compare;
  }

  /**
   * Return a string representation of this review. Use StringBuilder for efficiency.
   *
   * @return A string in the following format: Review by username on date Rating: rating reviewTitle
   * textOfReview Example: Review by Ben on Tue Aug 16 18:38:29 PDT 2016 Rating: 2 Very bad
   * experience Awaken by noises from top floor at 5AM. Lots of mosquitos too. <p> If the username
   * is null or empty, print "Anonymous" instead of the username
   */
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Review by ").append(username()).append(" on ").append(date())
        .append(System.lineSeparator());
    sb.append("Rating: ").append(rating()).append(System.lineSeparator());
    sb.append(reviewTitle()).append(System.lineSeparator());
    sb.append(review());
    return sb.toString();
  }

  public static Builder builder() {
    return new AutoValue_Review.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setHotelId(String hotelId);

    public abstract Builder setReviewId(String reviewId);

    public abstract Builder setRating(int rating);

    public abstract Builder setReviewTitle(String reviewTitle);

    public abstract Builder setReview(String review);

    public abstract Builder setIsRecom(boolean isRecom);

    public abstract Builder setDate(Date date);

    public abstract Builder setUsername(String username);

    public abstract Review build();
  }
}
