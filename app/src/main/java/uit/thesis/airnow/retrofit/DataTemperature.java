package uit.thesis.airnow.retrofit;

import com.google.gson.annotations.SerializedName;

public class DataTemperature {
  @SerializedName("time")
  private String time;

  public String getTime() {
    return time;
  }

  @SerializedName("degrees")
  private int degrees;

  public int getDegrees() {
    return degrees;
  }

  @SerializedName("location")
  private String location;

  public String getLocation() {
    return location;
  }
}
