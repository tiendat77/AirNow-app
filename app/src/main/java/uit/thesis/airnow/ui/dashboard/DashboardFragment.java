package uit.thesis.airnow.ui.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import uit.thesis.airnow.R;
import uit.thesis.airnow.ui.dashboard.chart.ChartItem;
import uit.thesis.airnow.ui.dashboard.chart.LineChartItem;

public class DashboardFragment extends Fragment {

  // <editor-fold desc="Declare variables">
  private DashboardViewModel dashboardViewModel;
  private ListView chartListView;

  private ArrayList<ChartItem> list = new ArrayList<>();
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
    chartListView = (ListView) root.findViewById(R.id.list_dashboard_chart);
  }

  private void initModel() {
    list.add(new LineChartItem(generateDataLine(1 + 1), getContext()));
    list.add(new LineChartItem(generateDataLine(2 + 1), getContext()));
    list.add(new LineChartItem(generateDataLine(3 + 1), getContext()));

    ChartDataAdapter cda = new ChartDataAdapter(getContext(), list);
    chartListView.setAdapter(cda);
  }

  /** adapter that supports 3 different item types */
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

}