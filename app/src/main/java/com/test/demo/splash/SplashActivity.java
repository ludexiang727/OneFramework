package com.test.demo.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.one.framework.MainActivity;
import com.one.framework.log.Logger;
import com.one.map.location.LocationProvider;
import com.one.map.location.LocationProvider.OnLocationChangedListener;
import com.one.map.model.Address;
import com.one.map.view.IMapView;
import com.one.map.view.IMapView.MapType;
import com.test.demo.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends AppCompatActivity {

  interface DialogClickListener {

    void onClickOk();

    void onClickCancel();
  }

  private boolean isNeedResumePermission;
  private boolean requestGettingOperationConfig = false;
  private AlertDialog mAlertDialog, mPermissionDialog, errorCodeDialog;
  private final int showErrorTimes = 5;
  private volatile int errorCount = 0;
  private boolean haveDoNext = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
//    print("SplashActivity onCreate()");
    setContentView(R.layout.splash_activity);
    SplashActivityPermissionsDispatcher.startLocationWithPermissionCheck(this);
  }

  @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION})
  void denyLocation() {
    showHintDialog(R.string.gps_neverlocation_hint);
  }

  @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION})
  void neverLocation() {
    showHintDialog(R.string.gps_neverlocation_hint);
  }

  @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION,
      Manifest.permission.ACCESS_FINE_LOCATION})
  public void startLocation() {
    LocationProvider.getInstance().addLocationChangeListener(locationChangedListener);
    LocationProvider provider = LocationProvider.getInstance();
    provider.buildLocation(this, IMapView.TENCENT);
    provider.start();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    SplashActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
  }


  @Override
  protected void onResume() {
    super.onResume();
    if (isNeedResumePermission) {
      isNeedResumePermission = false;
      SplashActivityPermissionsDispatcher.startLocationWithPermissionCheck(this);
    }
  }

  private void showHintDialog(int message) {
    if (mPermissionDialog == null || !mPermissionDialog.isShowing()) {
      showHintDialog(message, new DialogClickListener() {
        @Override
        public void onClickOk() {
          startActivity(getAppDetailSettingIntent());
          isNeedResumePermission = true;
        }

        @Override
        public void onClickCancel() {
          finish();
        }
      });
    }
  }

  @Keep
  private OnLocationChangedListener locationChangedListener = new OnLocationChangedListener() {
    @Override
    public void onLocationChanged(Address location) {
      if (location != null) {
        doNext();
      } else {
        onLocationFail();
      }
    }
  };

  private void doNext() {
//    print("SplashActivity doNext " + haveDoNext);
    if (haveDoNext) {
      return;
    }
    haveDoNext = true;
    finishActivity();
  }

  private void onLocationFail() {
    if (errorCodeDialog != null && errorCodeDialog.isShowing()) {
      return;
    }
    ++errorCount;
    if (errorCount > showErrorTimes) {
      showErrorDialog(getString(R.string.location_errorcode_62)).show();
      LocationProvider.getInstance().stop();
    }
  }

  public AlertDialog showErrorDialog(String msg) {
    if (errorCodeDialog == null) {
      AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style
          .Theme_AppCompat_Light_Dialog_Alert);
      errorCodeDialog = builder
          .setMessage(msg).setNegativeButton(android.R
                  .string.cancel, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  finish();
                }
              }

          ).setPositiveButton(R.string.action_settings,
              new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  openSettings();
                  isNeedResumePermission = true;
                  errorCount = 0;
                }
              }).create();
      errorCodeDialog.setCanceledOnTouchOutside(false);
    } else {
      errorCodeDialog.setMessage(msg);
    }
    return errorCodeDialog;
  }

  private void showHintDialog(int message, @NonNull final DialogClickListener dialogClickListener) {
    LocationProvider.getInstance().removeLocationChangedListener(locationChangedListener);
    LocationProvider.getInstance().stop();

    AlertDialog.Builder builder = new AlertDialog.Builder(this,
        R.style.Theme_AppCompat_Light_Dialog_Alert);
    mPermissionDialog = builder.setMessage(message)
        .setPositiveButton(android.R.string.ok, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            dialogClickListener.onClickOk();
          }
        }).setNegativeButton(android.R.string.cancel, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            dialogClickListener.onClickCancel();
          }

        }).create();
    mPermissionDialog.setCanceledOnTouchOutside(false);
    mPermissionDialog.setCancelable(false);
    mPermissionDialog.show();
    requestGettingOperationConfig = false;
  }

  private void finishActivity() {
//    print("SplashActivity finishActivity ");
    startMainActivity();
//    overridePendingTransition(0, R.anim.zoom_in_fade_out);
    finish();
  }

  private void startMainActivity() {
//    print("SplashActivity startMainActivity");
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }

  @Override
  public void finish() {
    super.finish();
    LocationProvider.getInstance().removeLocationChangedListener(locationChangedListener);
  }

  private Intent getAppDetailSettingIntent() {
    Intent localIntent = new Intent();
    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    localIntent.setData(Uri.fromParts("package", getPackageName(), null));
    return localIntent;
  }

  private void openSettings() {
    Intent localIntent = new Intent();
    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    localIntent.setAction(Settings.ACTION_SETTINGS);
    startActivity(localIntent);
  }

}
