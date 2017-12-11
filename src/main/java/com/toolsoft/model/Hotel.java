package com.toolsoft.model;

import com.google.auto.value.AutoValue;

/**
 * Represents a hotel. Stores hotelId, name, address, and averageRating. Implements {@link
 * Comparable} using the hotel name. If the names are the same, hotels are compared based on the
 * hotel id.
 */
@AutoValue
public abstract class Hotel implements Comparable<Hotel> {

  public abstract String id();

  public abstract String name();

  public abstract Address address();

  public abstract double rating();

  /**
   * Compare hotels based on the name (alphabetically). May use compareTo method in class String. If
   * the names are the same, compare based on the hotel ids.
   */
  @Override
  public int compareTo(Hotel o) {
    int compare = this.name().compareTo(o.name());
    if (compare == 0) {
      return this.id().compareTo(o.id());
    }
    return compare;
  }

  /**
   * Returns the string representation of the hotel in the following format: hotelName: hotelID
   * streetAddress city, state <p> Example: Travelodge Central San Francisco: 40682 1707 Market St
   * San Francisco, CA <p> Does not include information about the reviews.
   */
  public String toString() {
    return name() + ": " + id() + System.lineSeparator() + address();
  }

  public static Builder builder() {
    return new AutoValue_Hotel.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setId(String id);

    public abstract Builder setName(String name);

    public abstract Builder setAddress(Address address);

    public abstract Builder setRating(double rating);

    public abstract Hotel build();
  }
}
