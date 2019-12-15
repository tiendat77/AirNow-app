package uit.thesis.airnow.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uit.thesis.airnow.R;
import uit.thesis.airnow.model.AirVisualModel;
import uit.thesis.airnow.model.ForecastModel;
import uit.thesis.airnow.retrofit.APIService;
import uit.thesis.airnow.retrofit.APIUtils;
import uit.thesis.airnow.retrofit.DataClient;
import uit.thesis.airnow.util.ForecastAdapter;

public class HomeFragment extends Fragment {

  private HomeViewModel homeViewModel;

  // <editor-fold desc="Declare variables">

  private static final String TAG = "Home Fragment";

  /* Views */
  private AutoCompleteTextView locationsAutocomplete;
  private SwipeRefreshLayout swipeRefreshLayout;
  private ListView forecastListView;

  /* List */
  private ArrayList<ForecastModel> forecastModels = new ArrayList<ForecastModel>();
  private ForecastAdapter adapter;

  private ArrayList<String> locationsList = new ArrayList<>();
  private ArrayAdapter<String> locationsAdapter;
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
    locationsAutocomplete = root.findViewById(R.id.text_home_location);
    swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_home);
    forecastListView = root.findViewById(R.id.list_home_forecast);

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refresh("");
      }
    });
  }

  private void initModel() {
    getLocations();

    swipeRefreshLayout.setRefreshing(true);
    refresh("");

    locationsAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Snackbar.make(getActivity().findViewById(R.id.container), "Selected " + parent.getItemAtPosition(position), Snackbar.LENGTH_SHORT)
            .setAction("OK", new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                // Do something here
              }
            })
            .show();

        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        refresh(locationsList.get(position));
      }
    });

  }

  private void refresh(String location) {

    APIService APIService = APIUtils.getData();
    Call<DataClient> callback;
    if (location == "" || location == "None") {
      callback = APIService.getForecast();
    } else {
      callback = APIService.getForecast(location);
    }

    callback.enqueue(new Callback<DataClient>() {
      @Override
      public void onResponse(Call<DataClient> call, Response<DataClient> response) {
        if (response != null) {
          DataClient data = response.body();

          List<ForecastModel> forecastList = data.getForecastList();

          forecastModels.clear();

          forecastModels.addAll(forecastList);

          getAirVisual();

        }
      }

      @Override
      public void onFailure(Call<DataClient> call, Throwable t) {
        Log.d(TAG, t.getMessage());
        Snackbar.make(getActivity().findViewById(R.id.container), "Please check internet connection!", Snackbar.LENGTH_SHORT)
            .setAction("OK", new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                // Do something here
              }
            })
            .show();
      }
    });

    swipeRefreshLayout.setRefreshing(false);
  }

  private void getLocations() {
    APIService APIService = APIUtils.getData();
    Call<DataClient> callback = APIService.getLocations();
    callback.enqueue(new Callback<DataClient>() {
      @Override
      public void onResponse(Call<DataClient> call, Response<DataClient> response) {
        if (response != null) {
          DataClient data = response.body();

          List<String> locationModelList = data.getLocationsList();

          locationsList.clear();
          locationsList.add("None");
          locationsList.addAll(locationModelList);

          if (getActivity() != null) {
            locationsAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_location_dropdown, locationsList);
            locationsAutocomplete.setAdapter(locationsAdapter);
          }

        }
      }

      @Override
      public void onFailure(Call<DataClient> call, Throwable t) {
        Log.d(TAG, t.getMessage());
        Snackbar.make(getActivity().findViewById(R.id.container), "Please check internet connection!", Snackbar.LENGTH_SHORT)
            .setAction("OK", new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                // Do something here
              }
            })
            .show();
      }
    });
  }

  public void getAirVisual() {
    APIService APIService = APIUtils.getAirVisual();
    Call<DataClient> callback = APIService.getAirVisual("Vietnam", "Ho Chi Minh City", "Ho Chi Minh City", "e2579d92-3989-40e9-868a-693134233ed8");
    callback.enqueue(new Callback<DataClient>() {
      @Override
      public void onResponse(Call<DataClient> call, Response<DataClient> response) {
        DataClient data = response.body();

        AirVisualModel airVisualModel = data.getAirVisualModel();

        ForecastModel forecastModel = airVisualModel.getForecastModel();
        forecastModels.add(forecastModel);

        if (getActivity() != null) {
          adapter = new ForecastAdapter(getActivity(), forecastModels);
          forecastListView.setAdapter(adapter);
        }

        Log.d(TAG, airVisualModel.getCity() + "");
        Log.d(TAG, airVisualModel.getAqius() + "");
        Log.d(TAG, forecastModel.getStatus() + "");
        Log.d(TAG, airVisualModel.getTemperature() + "");

      }

      @Override
      public void onFailure(Call<DataClient> call, Throwable t) {
        Log.d(TAG, t.getMessage());
        Snackbar.make(getActivity().findViewById(R.id.container), "Please check internet connection!", Snackbar.LENGTH_SHORT)
            .setAction("OK", new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                // Do something here
              }
            })
            .show();
      }
    });
  }

}
