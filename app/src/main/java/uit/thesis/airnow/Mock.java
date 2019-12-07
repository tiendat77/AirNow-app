package uit.thesis.airnow;

import uit.thesis.airnow.model.ForecastModel;

public class Mock {

  public static ForecastModel[] forecastModels = new ForecastModel[]{
      new ForecastModel("Kyoto", 7, 12, 3, 52, "Good"),
      new ForecastModel("Singapore", 27, 54, 13.8, 78, "Moderate"),
      new ForecastModel("Shenyang", -11, 149, 55, 61, "Unhealthy for sensitive group"),
      new ForecastModel("Krakow", 8, 171, 95, 57, "Unhealthy"),
  };

  public static String[] locationModels = new String[] {
      "Kyoto",
      "Singapore",
      "Shenyang",
      "Krakow",
  };

}
