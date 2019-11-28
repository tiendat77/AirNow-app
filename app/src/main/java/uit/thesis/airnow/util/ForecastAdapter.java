package uit.thesis.airnow.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import uit.thesis.airnow.R;
import uit.thesis.airnow.model.Forecast;

public class ForecastAdapter extends ArrayAdapter<Forecast> {

  public ForecastAdapter(@NonNull Context context, ArrayList<Forecast> forecasts) {
    super(context, 0, forecasts);
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

    Forecast forecast = getItem(position);

    if (convertView == null) {
      convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_forecast, parent, false);
    }


    return convertView;
  }
}
