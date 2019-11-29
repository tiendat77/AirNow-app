package uit.thesis.airnow.ui.smartconfig;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.location.LocationManagerCompat;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import uit.thesis.airnow.R;

import static android.content.Context.WIFI_SERVICE;

public class SmartConfigEsp extends Fragment implements View.OnClickListener {

  private SmartConfigEspViewModel smartConfigEspViewModel;

  private static final String TAG = "ESP Smart Config";

  private static final int REQUEST_PERMISSION = 0x01;

  private TextView mApSsidTV;
  private TextView mApBssidTV;
  private EditText mApPasswordET;
  private EditText mDeviceCountET;
  private RadioGroup mPackageModeGroup;
  private TextView mMessageTV;
  private Button mConfirmBtn;

  private EsptouchAsyncTask4 mTask;

  private boolean mReceiverRegistered = false;
  private boolean mDestroyed = false;

  public static SmartConfigEsp newInstance() {
    return new SmartConfigEsp();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    smartConfigEspViewModel = ViewModelProviders.of(this).get(SmartConfigEspViewModel.class);
    View root = inflater.inflate(R.layout.fragment_smart_config_esp, container, false);

    initView(root);

    if (isSDKAtLeastP()) {
      if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        requestPermissions(permissions, REQUEST_PERMISSION);
      } else {
        registerBroadcastReceiver();
      }

    } else {
      registerBroadcastReceiver();
    }

    return root;
  }

  private void initView(View root) {

    mApSsidTV = root.findViewById(R.id.ap_ssid_text);
    mApBssidTV = root.findViewById(R.id.ap_bssid_text);
    mApPasswordET = root.findViewById(R.id.ap_password_edit);
    mDeviceCountET = root.findViewById(R.id.device_count_edit);
    mDeviceCountET.setText("1");
    mPackageModeGroup = root.findViewById(R.id.package_mode_group);
    mMessageTV = root.findViewById(R.id.message);
    mConfirmBtn = root.findViewById(R.id.confirm_btn);
    mConfirmBtn.setEnabled(false);
    mConfirmBtn.setOnClickListener(this);

  }

  private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action == null) {
        return;
      }

      WifiManager wifiManager = (WifiManager) context.getApplicationContext()
          .getSystemService(WIFI_SERVICE);
      assert wifiManager != null;

      switch (action) {
        case WifiManager.NETWORK_STATE_CHANGED_ACTION:
        case LocationManager.PROVIDERS_CHANGED_ACTION:
          onWifiChanged(wifiManager.getConnectionInfo());
          break;
      }
    }
  };

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == REQUEST_PERMISSION) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        if (!mDestroyed) {
          registerBroadcastReceiver();
        }
      }
      return;
    }

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }


  @Override
  public void onDestroyView() {
    super.onDestroyView();

    mDestroyed = true;
    if (mReceiverRegistered) {
      getActivity().unregisterReceiver(mReceiver);
    }
  }

  private boolean isSDKAtLeastP() {
    return Build.VERSION.SDK_INT >= 28;
  }

  private void registerBroadcastReceiver() {
    IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
    if (isSDKAtLeastP()) {
      filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
    }
    getActivity().registerReceiver(mReceiver, filter);
    mReceiverRegistered = true;
  }

  private void onWifiChanged(WifiInfo info) {
    boolean disconnected = info == null
        || info.getNetworkId() == -1
        || "<unknown ssid>".equals(info.getSSID());
    if (disconnected) {
      mApSsidTV.setText("");
      mApSsidTV.setTag(null);
      mApBssidTV.setText("");
      mMessageTV.setText(R.string.no_wifi_connection);
      mConfirmBtn.setEnabled(false);

      if (isSDKAtLeastP()) {
        checkLocation();
      }

      if (mTask != null) {
        mTask.cancelEsptouch();
        mTask = null;
        new AlertDialog.Builder(this.getContext())
            .setMessage(R.string.configure_wifi_change_message)
            .setNegativeButton(android.R.string.cancel, null)
            .show();
      }
    } else {
      String ssid = info.getSSID();
      if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
        ssid = ssid.substring(1, ssid.length() - 1);
      }
      mApSsidTV.setText(ssid);
      mApSsidTV.setTag(ByteUtil.getBytesByString(ssid));
      byte[] ssidOriginalData = TouchNetUtil.getOriginalSsidBytes(info);
      mApSsidTV.setTag(ssidOriginalData);

      String bssid = info.getBSSID();
      mApBssidTV.setText(bssid);

      mConfirmBtn.setEnabled(true);
      mMessageTV.setText("");
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        int frequency = info.getFrequency();
        if (frequency > 4900 && frequency < 5900) {
          // Connected 5G wifi. Device does not support 5G
          mMessageTV.setText(R.string.wifi_5g_message);
        }
      }
    }
  }

  private void checkLocation() {
    boolean enable;
    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    enable = locationManager != null && LocationManagerCompat.isLocationEnabled(locationManager);
    if (!enable) {
      mMessageTV.setText(R.string.location_disable_message);
    }
  }

  @Override
  public void onClick(View v) {
    if (v == mConfirmBtn) {
      byte[] ssid = mApSsidTV.getTag() == null ? ByteUtil.getBytesByString(mApSsidTV.getText().toString())
          : (byte[]) mApSsidTV.getTag();
      byte[] password = ByteUtil.getBytesByString(mApPasswordET.getText().toString());
      byte[] bssid = TouchNetUtil.parseBssid2bytes(mApBssidTV.getText().toString());
      byte[] deviceCount = mDeviceCountET.getText().toString().getBytes();
      byte[] broadcast = {(byte) (mPackageModeGroup.getCheckedRadioButtonId() == R.id.package_broadcast
          ? 1 : 0)};

      if (mTask != null) {
        mTask.cancelEsptouch();
      }
      mTask = new EsptouchAsyncTask4(this);
      mTask.execute(ssid, bssid, password, deviceCount, broadcast);
    }
  }

  private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
    private WeakReference<SmartConfigEsp> mActivity;

    private final Object mLock = new Object();
    private ProgressDialog mProgressDialog;
    private AlertDialog mResultDialog;
    private IEsptouchTask mEsptouchTask;

    EsptouchAsyncTask4(SmartConfigEsp activity) {
      mActivity = new WeakReference<>(activity);
    }

    void cancelEsptouch() {
      cancel(true);
      if (mProgressDialog != null) {
        mProgressDialog.dismiss();
      }
      if (mResultDialog != null) {
        mResultDialog.dismiss();
      }
      if (mEsptouchTask != null) {
        mEsptouchTask.interrupt();
      }
    }

    @Override
    protected void onPreExecute() {
      SmartConfigEsp activity = mActivity.get();
      mProgressDialog = new ProgressDialog(activity.getContext());
      mProgressDialog.setMessage(activity.getString(R.string.configuring_message));
      mProgressDialog.setCanceledOnTouchOutside(false);
      mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
          synchronized (mLock) {
            if (mEsptouchTask != null) {
              mEsptouchTask.interrupt();
            }
          }
        }
      });

      mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              synchronized (mLock) {
                if (mEsptouchTask != null) {
                  mEsptouchTask.interrupt();
                }
              }
            }
          });
      mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(IEsptouchResult... values) {
      SmartConfigEsp context = mActivity.get();
      if (context != null) {
        IEsptouchResult result = values[0];
        Log.i(TAG, "EspTouchResult: " + result);
        String text = result.getBssid() + " is connected to the wifi";
        Toast.makeText(context.getContext(), text, Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    protected List<IEsptouchResult> doInBackground(byte[]... params) {
      SmartConfigEsp activity = mActivity.get();
      int taskResultCount;
      synchronized (mLock) {
        byte[] apSsid = params[0];
        byte[] apBssid = params[1];
        byte[] apPassword = params[2];
        byte[] deviceCountData = params[3];
        byte[] broadcastData = params[4];
        taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
        Context context = activity.getContext();
        mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
        mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
//        mEsptouchTask.setEsptouchListener(this::publishProgress);
      }
      return mEsptouchTask.executeForResults(taskResultCount);
    }

    @Override
    protected void onPostExecute(List<IEsptouchResult> result) {
      SmartConfigEsp activity = mActivity.get();
      activity.mTask = null;
      mProgressDialog.dismiss();
      if (result == null) {
        mResultDialog = new AlertDialog.Builder(activity.getActivity())
            .setMessage(R.string.configure_result_failed_port)
            .setPositiveButton(android.R.string.ok, null)
            .show();
        mResultDialog.setCanceledOnTouchOutside(false);
        return;
      }

      // check whether the task is cancelled and no results received
      IEsptouchResult firstResult = result.get(0);
      if (firstResult.isCancelled()) {
        return;
      }
      // the task received some results including cancelled while
      // executing before receiving enough results

      if (!firstResult.isSuc()) {
        mResultDialog = new AlertDialog.Builder(activity.getActivity())
            .setMessage(R.string.configure_result_failed)
            .setPositiveButton(android.R.string.ok, null)
            .show();
        mResultDialog.setCanceledOnTouchOutside(false);
        return;
      }

      ArrayList<CharSequence> resultMsgList = new ArrayList<>(result.size());
      for (IEsptouchResult touchResult : result) {
        String message = activity.getString(R.string.configure_result_success_item,
            touchResult.getBssid(), touchResult.getInetAddress().getHostAddress());
        resultMsgList.add(message);
      }
      CharSequence[] items = new CharSequence[resultMsgList.size()];
      mResultDialog = new AlertDialog.Builder(activity.getActivity())
          .setTitle(R.string.configure_result_success)
          .setItems(resultMsgList.toArray(items), null)
          .setPositiveButton(android.R.string.ok, null)
          .show();
      mResultDialog.setCanceledOnTouchOutside(false);
    }
  }

}
