package uit.thesis.airnow.model;

public class Forecast {

    public String location;
    public double temperature;
    public int aqi;
    public double pollutant;
    public int humidity;
    public String status;

    public Forecast(String location, double temperature, int aqi, double pollutant, int humidity, String status) {
        this.location = location;
        this.temperature = temperature;
        this.aqi = aqi;
        this.pollutant = pollutant;
        this.humidity = humidity;
        this.status = status;
    }

}
