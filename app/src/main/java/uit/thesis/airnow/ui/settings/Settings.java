package uit.thesis.airnow.ui.settings;

import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import uit.thesis.airnow.Constants;
import uit.thesis.airnow.EsptouchActivity;
import uit.thesis.airnow.R;

public class Settings extends Fragment implements View.OnClickListener {

  private String TAG = "Setting";

  private SettingsViewModel mViewModel;

  public static Settings newInstance() {
    return new Settings();
  }

  // Declare variable here
  private MaterialButton smartConfigButton;
  private MaterialButton celsiusButton;
  private MaterialButton fahrenheitButton;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_settings, container, false);

    initView(root);
    initModel();

    return root;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
  }

  private void initView(View root) {
    smartConfigButton = root.findViewById(R.id.button_settings_smart_config);
    smartConfigButton.setOnClickListener(this);
    celsiusButton = root.findViewById(R.id.button_settings_celsius);
    celsiusButton.setOnClickListener(this);
    fahrenheitButton = root.findViewById(R.id.button_settings_fahrenheit);
    fahrenheitButton.setOnClickListener(this);
  }

  private void initModel() {
    if (getDegreesPreferences()) {
      celsiusButton.setChecked(true);
    } else {
      fahrenheitButton.setChecked(true);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_settings_smart_config: {
        Intent intent = new Intent(this.getActivity(), EsptouchActivity.class);
        startActivity(intent);
        break;
      }

      case R.id.button_settings_celsius: {
        setDegreesPreferences(true);
        break;
      }

      case R.id.button_settings_fahrenheit: {
        setDegreesPreferences(false);
        break;
      }
    }
  }

  private void setDegreesPreferences(boolean celsius) {
    SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences(Constants.PREFS_DEGREES, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    if (celsius) {
      editor.putBoolean(Constants.DEGREES_CELSIUS, true)
          .commit();
    } else {
      editor.putBoolean(Constants.DEGREES_CELSIUS, false)
          .commit();
    }
  }

  public boolean getDegreesPreferences() {
    SharedPreferences preferences = getContext().getSharedPreferences(Constants.PREFS_DEGREES, Context.MODE_PRIVATE);
    boolean result = true;
    if (preferences.contains(Constants.DEGREES_CELSIUS)) {
      result = preferences.getBoolean(Constants.DEGREES_CELSIUS, true);
    }

    return result;
  }
}
