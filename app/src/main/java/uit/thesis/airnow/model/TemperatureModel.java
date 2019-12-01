package uit.thesis.airnow.model;

import com.google.gson.annotations.SerializedName;

public class TemperatureModel {

  @SerializedName("time")
  private String time;

  @SerializedName("degrees")
  private double degrees;

  @SerializedName("location")
  private String location;

  public String getTime() {
    return time;
  }

  public double getDegrees() {
    return degrees;
  }

  public String getLocation() {
    return location;
  }
}
