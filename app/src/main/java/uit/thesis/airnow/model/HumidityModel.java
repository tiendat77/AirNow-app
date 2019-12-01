package uit.thesis.airnow.model;

import com.google.gson.annotations.SerializedName;

public class HumidityModel {

  @SerializedName("time")
  private String time;

  @SerializedName("humidity")
  private double humidity;

  @SerializedName("location")
  private String location;

  public String getTime() {
    return time;
  }

  public double getHumidity() {
    return humidity;
  }

  public String getLocation() {
    return location;
  }
}
