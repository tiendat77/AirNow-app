package uit.thesis.airnow.model;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

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

  private static DecimalFormat df2 = new DecimalFormat("#.##");
  private static DecimalFormat df1 = new DecimalFormat("#.#");

  public ForecastModel(String location, double temperature, int aqi, double pollutant, double humidity, String status) {
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

  public int getTemperature() {
    return (int) Math.round(temperature);
  }

  public String getTemperatureStringC() {
    return df1.format(temperature);
  }

  public String getTemperatureStringF() {
    double temperatureF = (double) (this.temperature * (1.8)) + 32.0;
    return df1.format(temperatureF);
  }

  public int getHumidity() {
    return (int) Math.round(humidity);
  }

  public String getHumidityString() {
    return df1.format(humidity);
  }

  public int getAqi() {
    return aqi;
  }

  public String getPollutant() {
    return df2.format(pollutant);
  }


  public String getStatus() {
    return status;
  }

  public String getTime() {
    return time;
  }
}
