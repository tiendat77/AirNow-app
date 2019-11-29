package uit.thesis.airnow.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uit.thesis.airnow.R;
import uit.thesis.airnow.retrofit.APIUtils;
import uit.thesis.airnow.retrofit.APIService;
import uit.thesis.airnow.retrofit.DataClient;

public class DashboardFragment extends Fragment {

  private DashboardViewModel dashboardViewModel;
  Button mbutton;

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    dashboardViewModel =
        ViewModelProviders.of(this).get(DashboardViewModel.class);
    View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
    final TextView textView = root.findViewById(R.id.text_dashboard);
    dashboardViewModel.getText().observe(this, new Observer<String>() {
      @Override
      public void onChanged(@Nullable String s) {
        textView.setText(s);
      }
    });
    mbutton = (Button) root.findViewById(R.id.test_button);
    mbutton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        APIService APIService = APIUtils.getData();
        Call<DataClient> callback = APIService.getAirdata(10, "Thủ Đức");
        callback.enqueue(new Callback<DataClient>() {
          @Override
          public void onResponse(Call<DataClient> call, Response<DataClient> response) {
            if (response != null) {
              Gson gson = new Gson();
              DataClient data = response.body();

              String dataJson = gson.toJson(data);
//                            String dataJson = gson.toJson(data.getDataAQIList());
              Log.d("Test", dataJson);
            }
          }

          @Override
          public void onFailure(Call<DataClient> call, Throwable t) {
            Log.d("Test_Err", t.getMessage());
          }
        });
      }
    });
    return root;
  }


}