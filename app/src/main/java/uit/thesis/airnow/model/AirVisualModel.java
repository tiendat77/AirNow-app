package uit.thesis.airnow.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AirVisualModel {

  @SerializedName("city")
  private String city;

  @SerializedName("current")
  @Expose
  private CurrentAirVisual currentAirVisual;

  static pm25aqiModel[] pm25aqi = {
      new pm25aqiModel(0.0, 12.0, 0, 50),
      new pm25aqiModel(12.1, 35.4, 51, 100),
      new pm25aqiModel(35.5, 55.4, 101, 150),
      new pm25aqiModel(55.5, 150.4, 151, 200),
      new pm25aqiModel(150.5, 250.4, 201, 300),
      new pm25aqiModel(250.5, 350.4, 301, 350),
      new pm25aqiModel(350.5, 500.4, 401, 500),
  };

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
    String city = "Ho Chi Minh (AirVisual)";
    double temperature = (double) getTemperature();
    int aqi = getAqius();
    double pollutant = aqi2pollutant(aqi);
    double humidity = getHumidity();
    String status = getStatus(aqi);
    return new ForecastModel(city, temperature, aqi, pollutant, humidity, status);
  }

  public String getStatus(int aqi) {
    String status = "";
    if (aqi > 0 && aqi < 51) {
      status = "Good";
    } else if (aqi > 50 && aqi < 101) {
      status = "Moderate";
    } else if (aqi > 100 && aqi < 151) {
      status = "Unhealthy for Sensitive Groups";
    } else if (aqi > 150 && aqi < 201) {
      status = "Unhealthy";
    } else if (aqi > 200 && aqi < 301) {
      status = "Very Unhealthy";
    } else if (aqi > 300) {
      status = "Hazardous";
    }
    return status;
  }

  public double aqi2pollutant(int aqi) {
    for (int i = 0; i < 7; i++) {
      if (aqi >= pm25aqi[i].llow && aqi <= pm25aqi[i].lhigh) {
        return ((pm25aqi[i].chigh - pm25aqi[i].clow) /
            (pm25aqi[i].lhigh - pm25aqi[i].llow)) *
            (aqi - pm25aqi[i].llow) +
            pm25aqi[i].clow;
      }
    }
    return 0;
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

// struct pm25aqi
class pm25aqiModel {
  public double clow;
  public double chigh;
  public int llow;
  public int lhigh;

  public pm25aqiModel() {

  }

  public pm25aqiModel(double clow, double chigh, int llow, int lhigh) {
    this.clow = clow;
    this.chigh = chigh;
    this.llow = llow;
    this.lhigh = lhigh;
  }
}