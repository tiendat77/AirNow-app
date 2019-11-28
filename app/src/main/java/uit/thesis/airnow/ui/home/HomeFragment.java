package uit.thesis.airnow.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import uit.thesis.airnow.Mock;
import uit.thesis.airnow.R;
import uit.thesis.airnow.model.Forecast;
import uit.thesis.airnow.util.ForecastAdapter;

public class HomeFragment extends Fragment {

  private HomeViewModel homeViewModel;

  // <editor-fold desc="Declare variables">

  private static final String TAG = "Home Fragment";

  /* Views */
  TextView locationText;
  ListView forecastListView;

  /* List */
  private ArrayList<Forecast> forecasts = new ArrayList<Forecast>();
  private ForecastAdapter adapter;

  // </editor-fold>

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    homeViewModel =
        ViewModelProviders.of(this).get(HomeViewModel.class);
    View root = inflater.inflate(R.layout.fragment_home, container, false);

    initView(root);
    initModel();

    return root;
  }

  private void initView(View root) {
    locationText = root.findViewById(R.id.text_home_location);
    forecastListView = root.findViewById(R.id.list_home_forecast);
  }

  private void initModel() {
    for (int i = 0; i < Mock.forecasts.length; i++) {
      forecasts.add(Mock.forecasts[i]);
    }

    adapter = new ForecastAdapter(this.getContext(), forecasts);

    forecastListView.setAdapter(adapter);

    Log.d(TAG, Mock.forecasts[0].location);

    Snackbar.make(getActivity().findViewById(R.id.container), "Done setup!", Snackbar.LENGTH_SHORT)
        .setAction("OK", new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            // Do something here
          }
        })
        .show();
  }

}
