package uit.thesis.airnow.ui.dashboard;

import android.content.Context;
import android.graphics.Color;
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

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uit.thesis.airnow.R;
import uit.thesis.airnow.model.AQIModel;
import uit.thesis.airnow.model.HumidityModel;
import uit.thesis.airnow.model.TemperatureModel;
import uit.thesis.airnow.retrofit.APIService;
import uit.thesis.airnow.retrofit.APIUtils;
import uit.thesis.airnow.retrofit.DataClient;
import uit.thesis.airnow.ui.dashboard.chart.ChartItem;
import uit.thesis.airnow.ui.dashboard.chart.LineChartItem;

public class DashboardFragment extends Fragment {

  // <editor-fold desc="Declare variables">
  private static final String TAG = "Dashboard Fragment";

  private DashboardViewModel dashboardViewModel;
  private SwipeRefreshLayout swipeRefreshLayout;
  private ListView chartListView;
  private AutoCompleteTextView locationsAutocomplete;

  private ChartDataAdapter cda;
  private ArrayList<ChartItem> list = new ArrayList<>();
  private ArrayList<Entry> aqiList = new ArrayList<>();
  private ArrayList<Entry> temperatureList = new ArrayList<>();
  private ArrayList<Entry> humidityList = new ArrayList<>();

  private ArrayList<String> locationsList = new ArrayList<>();
  private ArrayAdapter<String> locationsAdapter;

  private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  // </editor-fold>


  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    dashboardViewModel =
        ViewModelProviders.of(this).get(DashboardViewModel.class);
    View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

    initView(root);
    initModel();

    return root;
  }

  private void initView(View root) {
    swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh_dashboard);
    chartListView = (ListView) root.findViewById(R.id.list_dashboard_chart);
    locationsAutocomplete = (AutoCompleteTextView) root.findViewById(R.id.text_home_location);

//    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//      @Override
//      public void onRefresh() {
//        refresh();
//      }
//    });
  }

  private void initModel() {
    getLocations();

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
        refresh(parent.getItemAtPosition(position).toString());
      }
    });
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
          locationsList.addAll(locationModelList);

          locationsAdapter = new ArrayAdapter<>(getContext(), R.layout.item_location_dropdown, locationsList);
          locationsAutocomplete.setAdapter(locationsAdapter);

          locationsAutocomplete.setText(locationsList.get(0), false);
          swipeRefreshLayout.setRefreshing(true);
          refresh(locationsList.get(0));
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

  private void refresh(String location) {
    APIService APIService = APIUtils.getData();
    Call<DataClient> callback = APIService.getAirdata(28, location);
    callback.enqueue(new Callback<DataClient>() {
      @Override
      public void onResponse(Call<DataClient> call, Response<DataClient> response) {
        if (response != null) {
          DataClient data = response.body();

          List<AQIModel> aqiModelList = data.getDataAQIList();
          List<TemperatureModel> temperatureModelList = data.getTemperatureList();
          List<HumidityModel> humidityModelList = data.getHumidityList();

          aqiList.clear();
          temperatureList.clear();
          humidityList.clear();

          list.clear();
          list.add(new LineChartItem(getAqiData(aqiModelList), getContext()));
          list.add(new LineChartItem(getTemperatureData(temperatureModelList), getContext()));
          list.add(new LineChartItem(getHumidityData(humidityModelList), getContext()));
          cda = new ChartDataAdapter(getContext(), list);
          chartListView.setAdapter(cda);
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

  private LineData getAqiData(List<AQIModel> aqiModels) {
    ArrayList<Entry> values = new ArrayList<>();


    for (int i = 0; i < aqiModels.size(); i++) {
      values.add(new Entry(i, aqiModels.get(i).getAqi()));
    }

    LineDataSet d = new LineDataSet(values, "AQI in month");
    d.setLineWidth(2.5f);
    d.setCircleRadius(4.5f);
    d.setHighLightColor(Color.rgb(244, 117, 117));
    d.setDrawValues(false);
    d.setColor(ColorTemplate.COLORFUL_COLORS[0]);
    d.setCircleColor(ColorTemplate.COLORFUL_COLORS[0]);

    ArrayList<ILineDataSet> sets = new ArrayList<>();
    sets.add(d);

    return new LineData(sets);
  }

  private LineData getTemperatureData(List<TemperatureModel> temperatureModels) {
    ArrayList<Entry> values = new ArrayList<>();


    for (int i = 0; i < temperatureModels.size(); i++) {
      values.add(new Entry(i, (float) temperatureModels.get(i).getDegrees()));
    }

    LineDataSet d = new LineDataSet(values, "Temperature in month");
    d.setLineWidth(2.5f);
    d.setCircleRadius(4.5f);
    d.setHighLightColor(Color.rgb(244, 117, 117));
    d.setDrawValues(false);
    d.setColor(ColorTemplate.COLORFUL_COLORS[1]);
    d.setCircleColor(ColorTemplate.COLORFUL_COLORS[1]);

    ArrayList<ILineDataSet> sets = new ArrayList<>();
    sets.add(d);

    return new LineData(sets);
  }

  private LineData getHumidityData(List<HumidityModel> humidityModels) {
    ArrayList<Entry> values = new ArrayList<>();


    for (int i = 0; i < humidityModels.size(); i++) {
      values.add(new Entry(i, (float) humidityModels.get(i).getHumidity()));
    }

    LineDataSet d = new LineDataSet(values, "Humidity in month");
    d.setLineWidth(2.5f);
    d.setCircleRadius(4.5f);
    d.setHighLightColor(Color.rgb(244, 117, 117));
    d.setDrawValues(false);
    d.setColor(ColorTemplate.COLORFUL_COLORS[2]);
    d.setCircleColor(ColorTemplate.COLORFUL_COLORS[2]);

    ArrayList<ILineDataSet> sets = new ArrayList<>();
    sets.add(d);

    return new LineData(sets);
  }

  /**
   * generates a random ChartData object with just one DataSet
   *
   * @return Line data
   */
  private LineData generateDataLine(int cnt) {

    ArrayList<Entry> values1 = new ArrayList<>();

    for (int i = 0; i < 19; i++) {
      values1.add(new Entry(i, (int) (Math.random() * 65) + 40));
    }

    LineDataSet d1 = new LineDataSet(values1, cnt + ", (1)");
    d1.setLineWidth(2.5f);
    d1.setCircleRadius(4.5f);
    d1.setHighLightColor(Color.rgb(244, 117, 117));
    d1.setDrawValues(false);

    ArrayList<ILineDataSet> sets = new ArrayList<>();
    sets.add(d1);

    return new LineData(sets);
  }

  /**
   * adapter that supports 3 different item types
   */
  private class ChartDataAdapter extends ArrayAdapter<ChartItem> {

    ChartDataAdapter(Context context, List<ChartItem> objects) {
      super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
      //noinspection ConstantConditions
      return getItem(position).getView(position, convertView, getContext());
    }

    @Override
    public int getItemViewType(int position) {
      // return the views type
      ChartItem ci = getItem(position);
      return ci != null ? ci.getItemType() : 0;
    }

    @Override
    public int getViewTypeCount() {
      return 1; // we have 3 different item-types
    }
  }

}