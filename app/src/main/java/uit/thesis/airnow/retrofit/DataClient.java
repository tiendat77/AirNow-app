package uit.thesis.airnow.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataClient {

  @SerializedName("aqi")
  private List<DataAQI> dataAQIList;

  public List<DataAQI> getDataAQIList() {

    return dataAQIList;
  }

  @SerializedName("forecast")
  private List<DataForecast> dataForecastList;

  public List<DataForecast> getDataForecastList() {
    return dataForecastList;
  }

  @SerializedName("humidity")
  private List<DataHumidity> dataHumidityList;

  public List<DataHumidity> getDataHumidityList() {

    return dataHumidityList;
  }

  @SerializedName("temperature")
  private List<DataTemperature> dataTemperatureList;

  public List<DataTemperature> getDataTemperatureList() {

    return dataTemperatureList;
  }

  @SerializedName("locations")
  private List<String> dataLocations;

  public List<String> getDataLocationsList() {

    return dataLocations;
  }


}

