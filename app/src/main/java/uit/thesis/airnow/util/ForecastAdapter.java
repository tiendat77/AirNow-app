package uit.thesis.airnow.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import uit.thesis.airnow.R;
import uit.thesis.airnow.model.ForecastModel;

public class ForecastAdapter extends ArrayAdapter<ForecastModel> {

  public ForecastAdapter(@NonNull Context context, ArrayList<ForecastModel> forecastModels) {
    super(context, 0, forecastModels);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

    ForecastModel forecastModel = getItem(position);

    TextView textForecastLocation;
    TextView textForecastTemperature;
    TextView textForecastHumidity;
    TextView textForecastAqi;
    TextView textForecastAqiType;
    TextView textForecastStatus;
    TextView textForecastPollutant;

    ImageView imageForecastAqiFace;
    LinearLayout layoutForecastAqiFaceBg;
    LinearLayout layoutForecastAqiBg;

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_forecast, parent, false);
    }

    textForecastLocation = convertView.findViewById(R.id.text_forecast_location);
    textForecastTemperature = convertView.findViewById(R.id.text_forecast_temperature);
    textForecastHumidity = convertView.findViewById(R.id.text_forecast_humidity);
    textForecastAqi = convertView.findViewById(R.id.text_forecast_aqi);
    textForecastAqiType = convertView.findViewById(R.id.text_forecast_aqi_type);
    textForecastStatus = convertView.findViewById(R.id.text_forecast_status);
    textForecastPollutant = convertView.findViewById(R.id.text_forecast_pollutant);

    imageForecastAqiFace = convertView.findViewById(R.id.image_forecast_aqi_face);
    layoutForecastAqiFaceBg = convertView.findViewById(R.id.layout_forecast_aqi_face_bg);
    layoutForecastAqiBg = convertView.findViewById(R.id.layout_forecast_aqi_bg);

    textForecastLocation.setText(forecastModel.getLocation());
    textForecastTemperature.setText(forecastModel.getTemperature() + "°");
    textForecastHumidity.setText(forecastModel.getHumidity() + "%");
    textForecastAqi.setText((String.valueOf((forecastModel.getAqi()))));
    textForecastStatus.setText(forecastModel.getStatus());
    textForecastPollutant.setText("PM2.5 | " + forecastModel.getPollutant() + " µg/m³");

    switch (forecastModel.getStatus()) {
      case "Good": {
        textForecastAqi.setTextColor(getContext().getResources().getColor(R.color.card_text_lv1));
        textForecastAqiType.setTextColor(getContext().getResources().getColor(R.color.card_text_lv1));
        textForecastStatus.setTextColor(getContext().getResources().getColor(R.color.card_text_lv1));
        textForecastPollutant.setTextColor(getContext().getResources().getColor(R.color.card_text_lv1));

        layoutForecastAqiBg.setBackgroundColor(getContext().getResources().getColor(R.color.card_bg_lv1));
        layoutForecastAqiFaceBg.setBackgroundColor(getContext().getResources().getColor(R.color.card_face_lv1));
        imageForecastAqiFace.setImageResource(R.drawable.ic_face_green);
        break;
      }

      case "Moderate": {
        textForecastAqi.setTextColor(getContext().getResources().getColor(R.color.card_text_lv2));
        textForecastAqiType.setTextColor(getContext().getResources().getColor(R.color.card_text_lv2));
        textForecastStatus.setTextColor(getContext().getResources().getColor(R.color.card_text_lv2));
        textForecastPollutant.setTextColor(getContext().getResources().getColor(R.color.card_text_lv2));

        layoutForecastAqiBg.setBackgroundColor(getContext().getResources().getColor(R.color.card_bg_lv2));
        layoutForecastAqiFaceBg.setBackgroundColor(getContext().getResources().getColor(R.color.card_face_lv2));
        imageForecastAqiFace.setImageResource(R.drawable.ic_face_yellow);
        break;
      }

      case "Unhealthy for sensitive group": {
        textForecastAqi.setTextColor(getContext().getResources().getColor(R.color.card_text_lv3));
        textForecastAqiType.setTextColor(getContext().getResources().getColor(R.color.card_text_lv3));
        textForecastStatus.setTextColor(getContext().getResources().getColor(R.color.card_text_lv3));
        textForecastPollutant.setTextColor(getContext().getResources().getColor(R.color.card_text_lv3));

        layoutForecastAqiBg.setBackgroundColor(getContext().getResources().getColor(R.color.card_bg_lv3));
        layoutForecastAqiFaceBg.setBackgroundColor(getContext().getResources().getColor(R.color.card_face_lv3));
        imageForecastAqiFace.setImageResource(R.drawable.ic_face_orange);
        break;
      }

      case "Unhealthy": {
        textForecastAqi.setTextColor(getContext().getResources().getColor(R.color.card_text_lv4));
        textForecastAqiType.setTextColor(getContext().getResources().getColor(R.color.card_text_lv4));
        textForecastStatus.setTextColor(getContext().getResources().getColor(R.color.card_text_lv4));
        textForecastPollutant.setTextColor(getContext().getResources().getColor(R.color.card_text_lv4));

        layoutForecastAqiBg.setBackgroundColor(getContext().getResources().getColor(R.color.card_bg_lv4));
        layoutForecastAqiFaceBg.setBackgroundColor(getContext().getResources().getColor(R.color.card_face_lv4));
        imageForecastAqiFace.setImageResource(R.drawable.ic_face_red);
        break;
      }
    }

    return convertView;
  }

  public void initItemView() {

  }
}
