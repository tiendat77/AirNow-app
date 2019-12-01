package uit.thesis.airnow.model;

import com.google.gson.annotations.SerializedName;

public class LocationModel {

  @SerializedName("location")
  public String location;

  public LocationModel(String location) {
    this.location = location;
  }

  public String getLocation() {
    return location;
  }
}
