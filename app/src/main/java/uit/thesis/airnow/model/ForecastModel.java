package uit.thesis.airnow.model;

public class ForecastModel {

    public String location;
    public double temperature;
    public int aqi;
    public double pollutant;
    public int humidity;
    public String status;

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

    public int getHumidity() {
        return humidity;
    }

    public String getStatus() {
        return status;
    }
}
