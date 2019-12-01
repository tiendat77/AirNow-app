package uit.thesis.airnow.retrofit;

import com.google.gson.annotations.SerializedName;

public class DataForecast {

  @SerializedName("time")
  private String time;

  public String getTime() {

    return time;
  }

  @SerializedName("aqi")
  private int aqi;

  public int getAqi() {

    return aqi;
  }

  @SerializedName("status")
  private String status;

  public String getStatus() {

    return status;
  }

  @SerializedName("temperature")
  private double temperature;

  public double getTemperature() {

    return temperature;
  }

  @SerializedName("humidity")
  private double humidity;

  public double getHumidity() {

    return humidity;
  }

  @SerializedName("pollutant")
  private double pollutant;

  public double getPollutant() {

    return pollutant;
  }

  @SerializedName("location")
  private String location;

  public String getLocation() {

    return location;
  }
}
