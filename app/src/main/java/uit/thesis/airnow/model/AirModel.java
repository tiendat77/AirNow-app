package uit.thesis.airnow.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AirModel {

  public String location;
  public double value;
  public Date time;
  private DateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");

  public AirModel(String location, double value, Date time) {
    this.location = location;
    this.value = value;
    this.time = time;
  }
}
