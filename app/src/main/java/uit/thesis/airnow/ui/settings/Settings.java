package uit.thesis.airnow.ui.settings;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import uit.thesis.airnow.EsptouchActivity;
import uit.thesis.airnow.R;

public class Settings extends Fragment implements View.OnClickListener {

  private String TAG = "Setting";

  private SettingsViewModel mViewModel;

  public static Settings newInstance() {
    return new Settings();
  }

  // Declare variable here
  MaterialButton smartConfigButton;

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_settings, container, false);

    initView(root);

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
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_settings_smart_config: {
        Intent intent = new Intent(this.getActivity(), EsptouchActivity.class);
        startActivity(intent);
        break;
      }
    }
  }
}
