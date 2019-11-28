package uit.thesis.airnow.retrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataClient {

    @SerializedName("aqi")
    private List<DataAQI> dataAQIList;

    public List<DataAQI> getDataAQIList() {
        return dataAQIList;
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

}

