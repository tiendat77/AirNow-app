package uit.thesis.airnow.model;

import com.google.gson.annotations.SerializedName;

public class AQIModel {

  @SerializedName("time")
  private String time;

  @SerializedName("aqi")
  private int aqi;

  @SerializedName("description")
  private String description;

  @SerializedName("pollutant")
  private double pollutant;

  @SerializedName("location")
  private String location;

  public String getTime() {
    return time;
  }

  public int getAqi() {
    return aqi;
  }

  public String getDescription() {
    return description;
  }

  public double getPollutant() {
    return pollutant;
  }

  public String getLocation() {
    return location;
  }
}
