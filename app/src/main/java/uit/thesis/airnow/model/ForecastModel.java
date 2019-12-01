package uit.thesis.airnow.model;

import com.google.gson.annotations.SerializedName;

public class ForecastModel {

  @SerializedName("aqi")
  private int aqi;

  @SerializedName("humidity")
  private double humidity;

  @SerializedName("location")
  private String location;

  @SerializedName("temperature")
  private double temperature;

  @SerializedName("pollutant")
  private double pollutant;

  @SerializedName("status")
  private String status;

  @SerializedName("time")
  private String time;

  public ForecastModel(String location, double temperature, int aqi, double pollutant, int humidity, String status) {
    this.location = location;
    this.temperature = temperature;
    this.aqi = aqi;
    this.pollutant = pollutant;
    this.humidity = humidity;
    this.status = status;
  }

  public String getLocation() {
    return location;
  }

  public double getTemperature() {
    return temperature;
  }

  public int getAqi() {
    return aqi;
  }

  public double getPollutant() {
    return pollutant;
  }

  public double getHumidity() {
    return humidity;
  }

  public String getStatus() {
    return status;
  }

  public String getTime() {
    return time;
  }
}
