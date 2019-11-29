package uit.thesis.airnow;

import uit.thesis.airnow.model.Forecast;

public class Mock {

  public static String forecastJson = "{}";

  public static Forecast[] forecasts = new Forecast[]{
      new Forecast("Kyoto", 7, 12, 3, 52, "Good"),
      new Forecast("Singapore", 27, 54, 13.8, 78, "Moderate"),
      new Forecast("Shenyang", -11, 149, 55, 61, "Unhealthy for sensitive group"),
      new Forecast("Krakow", 8, 171, 95, 57, "Unhealthy"),
  };

}
