package uit.thesis.airnow;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.task.__IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class EsptouchActivity extends AppCompatActivity implements OnClickListener {

  private static final String TAG = "EsptouchActivity";

  private static final int REQUEST_PERMISSION = 0x01;

  private TextView mApSsidTV;
  private TextView mApBssidTV;
  private EditText mApPasswordET;
  private EditText mDeviceCountET;
  private RadioGroup mPackageModeGroup;
  private TextView mMessageTV;
  private Button mConfirmBtn;

  private IEsptouchListener myListener = new IEsptouchListener() {

    @Override
    public void onEsptouchResultAdded(final IEsptouchResult result) {
      onEsptoucResultAddedPerform(result);
    }
  };

  private EsptouchAsyncTask4 mTask;

  private boolean mReceiverRegistered = false;
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
          WifiInfo wifiInfo;
          if (intent.hasExtra(WifiManager.EXTRA_WIFI_INFO)) {
            wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
          } else {
            wifiInfo = wifiManager.getConnectionInfo();
          }
          onWifiChanged(wifiInfo);
          break;
        case LocationManager.PROVIDERS_CHANGED_ACTION:
          onWifiChanged(wifiManager.getConnectionInfo());
          break;
      }
    }
  };

  private boolean mDestroyed = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_esp_touch);

    mApSsidTV = findViewById(R.id.ap_ssid_text);
    mApBssidTV = findViewById(R.id.ap_bssid_text);
    mApPasswordET = findViewById(R.id.ap_password_edit);
    mDeviceCountET = findViewById(R.id.device_count_edit);
    mDeviceCountET.setText("1");
    mPackageModeGroup = findViewById(R.id.package_mode_group);
    mMessageTV = findViewById(R.id.message);
    mConfirmBtn = findViewById(R.id.confirm_btn);
    mConfirmBtn.setEnabled(false);
    mConfirmBtn.setOnClickListener(this);

    if (isSDKAtLeastP()) {
      if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {
        String[] permissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION
        };

        requestPermissions(permissions, REQUEST_PERMISSION);
      } else {
        registerBroadcastReceiver();
      }

    } else {
      registerBroadcastReceiver();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode) {
      case REQUEST_PERMISSION:
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          if (!mDestroyed) {
            registerBroadcastReceiver();
          }
        }
        break;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    mDestroyed = true;
    if (mReceiverRegistered) {
      unregisterReceiver(mReceiver);
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
    registerReceiver(mReceiver, filter);
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
        new AlertDialog.Builder(EsptouchActivity.this)
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
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (locationManager == null) {
      enable = false;
    } else {
      boolean locationGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
      boolean locationNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
      enable = locationGPS || locationNetwork;
    }

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

  private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
    runOnUiThread(new Runnable() {

      @Override
      public void run() {
        String text = result.getBssid() + " is connected to the wifi";
        Toast.makeText(EsptouchActivity.this, text,
            Toast.LENGTH_SHORT).show();
      }

    });
  }

  private static class EsptouchAsyncTask4 extends AsyncTask<byte[], Void, List<IEsptouchResult>> {
    private WeakReference<EsptouchActivity> mActivity;

    // without the lock, if the user tap confirm and cancel quickly enough,
    // the bug will arise. the reason is follows:
    // 0. task is starting created, but not finished
    // 1. the task is cancel for the task hasn't been created, it do nothing
    // 2. task is created
    // 3. Oops, the task should be cancelled, but it is running
    private final Object mLock = new Object();
    private ProgressDialog mProgressDialog;
    private AlertDialog mResultDialog;
    private IEsptouchTask mEsptouchTask;

    EsptouchAsyncTask4(EsptouchActivity activity) {
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
      Activity activity = mActivity.get();
      mProgressDialog = new ProgressDialog(activity);
      mProgressDialog.setMessage(activity.getString(R.string.configuring_message));
      mProgressDialog.setCanceledOnTouchOutside(false);
      mProgressDialog.setOnCancelListener(new OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
          synchronized (mLock) {
            if (__IEsptouchTask.DEBUG) {
              Log.i(TAG, "progress dialog back pressed canceled");
            }
            if (mEsptouchTask != null) {
              mEsptouchTask.interrupt();
            }
          }
        }
      });
      mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              synchronized (mLock) {
                if (__IEsptouchTask.DEBUG) {
                  Log.i(TAG, "progress dialog cancel button canceled");
                }
                if (mEsptouchTask != null) {
                  mEsptouchTask.interrupt();
                }
              }
            }
          });
      mProgressDialog.show();
    }

    @Override
    protected List<IEsptouchResult> doInBackground(byte[]... params) {
      EsptouchActivity activity = mActivity.get();
      int taskResultCount;
      synchronized (mLock) {
        byte[] apSsid = params[0];
        byte[] apBssid = params[1];
        byte[] apPassword = params[2];
        byte[] deviceCountData = params[3];
        byte[] broadcastData = params[4];
        taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
        Context context = activity.getApplicationContext();
        mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
        mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
        mEsptouchTask.setEsptouchListener(activity.myListener);
      }
      return mEsptouchTask.executeForResults(taskResultCount);
    }

    @Override
    protected void onPostExecute(List<IEsptouchResult> result) {
      EsptouchActivity activity = mActivity.get();
      mProgressDialog.dismiss();
      if (result == null) {
        mResultDialog = new AlertDialog.Builder(activity)
            .setMessage(R.string.configure_result_failed_port)
            .setPositiveButton(android.R.string.ok, null)
            .show();
        mResultDialog.setCanceledOnTouchOutside(false);
        return;
      }

      IEsptouchResult firstResult = result.get(0);
      // check whether the task is cancelled and no results received
      if (!firstResult.isCancelled()) {
        // the task received some results including cancelled while
        // executing before receiving enough results
        if (firstResult.isSuc()) {
          ArrayList<CharSequence> resultMsgList = new ArrayList<>(result.size());
          for (IEsptouchResult touchResult : result) {
            String message = activity.getString(R.string.configure_result_success_item,
                touchResult.getBssid(), touchResult.getInetAddress().getHostAddress());
            resultMsgList.add(message);
          }

          CharSequence[] items = new CharSequence[resultMsgList.size()];
          mResultDialog = new AlertDialog.Builder(activity)
              .setTitle(R.string.configure_result_success)
              .setItems(resultMsgList.toArray(items), null)
              .setPositiveButton(android.R.string.ok, null)
              .show();
          mResultDialog.setCanceledOnTouchOutside(false);
        } else {
          mResultDialog = new AlertDialog.Builder(activity)
              .setMessage(R.string.configure_result_failed)
              .setPositiveButton(android.R.string.ok, null)
              .show();
          mResultDialog.setCanceledOnTouchOutside(false);
        }
      }

      activity.mTask = null;
    }
  }

}
