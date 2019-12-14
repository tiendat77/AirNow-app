package uit.thesis.airnow.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

  @GET("airdata")
  Call<DataClient> getAirdata(@Query("range") int range, @Query("location") String location);

  @GET("locations")
  Call<DataClient> getLocations();

  @GET("forecast")
  Call<DataClient> getForecast();

  @GET("forecast")
  Call<DataClient> getForecast(@Query("location") String location);

  @GET("city")
  Call<DataClient> getAirVisual(@Query("country") String country, @Query("state") String state, @Query("city") String city, @Query("key") String key);
}
