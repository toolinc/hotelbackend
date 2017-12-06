package com.toolsoft.model;

import com.google.auto.value.AutoValue;

/**
 * The class that represents an address of a hotel in USA. It contains the following data elements:
 * city, state, street address, latitude and longitude.
 */
@AutoValue
public abstract class Address {

  public abstract String city();

  public abstract String state();

  public abstract String streetAddress();

  public abstract double lat();

  public abstract double lon();

  public static Builder builder() {
    return new AutoValue_Address.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setCity(String city);

    public abstract Builder setState(String state);

    public abstract Builder setStreetAddress(String streetAddress);

    public abstract Builder setLat(double lat);

    public abstract Builder setLon(double lon);

    public abstract Address build();
  }
}
