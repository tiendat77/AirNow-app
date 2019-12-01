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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uit.thesis.airnow.Mock;
import uit.thesis.airnow.R;
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
  TextView locationText;
  SwipeRefreshLayout swipeRefreshLayout;
  ListView forecastListView;

  /* List */
  private ArrayList<ForecastModel> forecastModels = new ArrayList<ForecastModel>();
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
    swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_home);
    forecastListView = root.findViewById(R.id.list_home_forecast);

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        refresh();
      }
    });
  }

  private void initModel() {
    swipeRefreshLayout.setRefreshing(true);

    refresh();
  }

  private void refresh() {

    APIService APIService = APIUtils.getData();
    Call<DataClient> callback = APIService.getForecast();
    callback.enqueue(new Callback<DataClient>() {
      @Override
      public void onResponse(Call<DataClient> call, Response<DataClient> response) {
        if (response != null) {
          DataClient data = response.body();

          List<ForecastModel> forecastList = data.getForecastList();

          forecastModels.clear();

          forecastModels.addAll(forecastList);

          adapter = new ForecastAdapter(getContext(), forecastModels);

          forecastListView.setAdapter(adapter);
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

}
