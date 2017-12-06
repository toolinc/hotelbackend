package com.toolsoft.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class TouristAttraction {

  public abstract String id();

  public abstract String name();

  public abstract String address();

  public abstract double rating();

  public static Builder builder() {
    return new AutoValue_TouristAttraction.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setId(String id);

    public abstract Builder setName(String name);

    public abstract Builder setAddress(String address);

    public abstract Builder setRating(double rating);

    public abstract TouristAttraction build();
  }
}
