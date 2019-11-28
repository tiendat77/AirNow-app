package uit.thesis.airnow.retrofit;

import com.google.gson.annotations.SerializedName;

public class DataAQI {

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

    @SerializedName("description")
    private String description;

    public String getDescription() {
        return description;
    }

    @SerializedName("pollutant")
    private float pollutant;

    public float getPollutant() {
        return pollutant;
    }
    @SerializedName("location")
    private String location;

    public String getLocation() {
        return location;
    }
}
