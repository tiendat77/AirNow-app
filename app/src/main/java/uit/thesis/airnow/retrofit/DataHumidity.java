package uit.thesis.airnow.retrofit;

import com.google.gson.annotations.SerializedName;

public class DataHumidity {
  @SerializedName("time")
  private String time;

  public String getTime() {

    return time;
  }

  @SerializedName("humidity")
  private double humidity;

  public double getHumidity() {

    return humidity;
  }

  @SerializedName("location")
  private String location;

  public String getLocation() {

    return location;
  }
}

