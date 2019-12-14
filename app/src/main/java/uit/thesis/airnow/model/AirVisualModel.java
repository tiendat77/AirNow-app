package uit.thesis.airnow.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AirVisualModel {

  @SerializedName("city")
  private String city;

  @SerializedName("current")
  @Expose
  private CurrentAirVisual currentAirVisual;

  public int getTemperature() {
    return currentAirVisual.getWeather().getTp();
  }

  public int getHumidity() {
    return currentAirVisual.getWeather().getHu();
  }

  public int getAqius() {
    return currentAirVisual.getPollution().getAqius();
  }

  public String getCity() {
    return city;
  }

  public ForecastModel getForecastModel() {
    String city = this.city + "(AV)";
    double temperature = (double) getTemperature();
    int aqi = getAqius();
    double pollutant = aqi2pollutant(aqi);
    double humidity = getHumidity();
    String status = getStatus(aqi);
    return new ForecastModel(city, temperature, aqi, pollutant, humidity, status);
  }

  public String getStatus(int aqi) {
    String status = "";
    // TODO: implement action here
    return status;
  }

  public double aqi2pollutant(int aqi) {
    double result = (double) 0.1 * aqi;
    // TODO: implement action here
    return result;
  }

}

class CurrentAirVisual {
  @SerializedName("weather")
  @Expose
  private Weather weather;

  @SerializedName("pollution")
  @Expose
  private Pollution pollution;

  public Weather getWeather() {
    return weather;
  }

  public Pollution getPollution() {
    return pollution;
  }
}

class Weather {
  @SerializedName("tp")
  @Expose
  private int tp;

  @SerializedName("hu")
  @Expose
  private int hu;

  public int getTp() {
    return tp;
  }

  public int getHu() {
    return hu;
  }
}

class Pollution {
  @SerializedName("aqius")
  @Expose
  private int aqius;

  @SerializedName("ts")
  @Expose
  private String ts;

  public int getAqius() {
    return aqius;
  }

  public String getTs() {
    return ts;
  }
}
