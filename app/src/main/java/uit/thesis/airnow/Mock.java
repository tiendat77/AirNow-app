package uit.thesis.airnow;

import uit.thesis.airnow.model.ForecastModel;
import uit.thesis.airnow.model.LocationModel;

public class Mock {

  public static String forecastJson = "{}";

  public static ForecastModel[] forecastModels = new ForecastModel[]{
      new ForecastModel("Kyoto", 7, 12, 3, 52, "Good"),
      new ForecastModel("Singapore", 27, 54, 13.8, 78, "Moderate"),
      new ForecastModel("Shenyang", -11, 149, 55, 61, "Unhealthy for sensitive group"),
      new ForecastModel("Krakow", 8, 171, 95, 57, "Unhealthy"),
  };

  public static LocationModel[] locationModels = new LocationModel[] {
      new LocationModel("Kyoto"),
      new LocationModel("Singapore"),
      new LocationModel("Shenyang"),
      new LocationModel("Krakow"),
  };

}
