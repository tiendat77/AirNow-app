package uit.thesis.airnow.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import uit.thesis.airnow.model.AQIModel;
import uit.thesis.airnow.model.ForecastModel;
import uit.thesis.airnow.model.HumidityModel;
import uit.thesis.airnow.model.LocationModel;
import uit.thesis.airnow.model.TemperatureModel;

public class DataClient {

  @SerializedName("aqi")
  private List<AQIModel> dataAQIList;

  @SerializedName("forecast")
  private List<ForecastModel> dataForecastList;


  @SerializedName("humidity")
  private List<HumidityModel> dataHumidityList;

  @SerializedName("temperature")
  private List<TemperatureModel> dataTemperatureList;

  @SerializedName("locations")
  private List<LocationModel> dataLocations;

  public List<AQIModel> getDataAQIList() {
    return dataAQIList;
  }

  public List<ForecastModel> getDataForecastList() {
    return dataForecastList;
  }

  public List<HumidityModel> getDataHumidityList() {
    return dataHumidityList;
  }

  public List<TemperatureModel> getDataTemperatureList() {
    return dataTemperatureList;
  }

  public List<LocationModel> getDataLocations() {
    return dataLocations;
  }
}

