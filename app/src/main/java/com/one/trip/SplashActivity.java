package com.one.trip;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.MainActivity;
import com.one.framework.app.base.AbsBaseActivity;
import com.one.map.location.LocationProvider;
import com.one.map.location.LocationProvider.OnLocationChangedListener;
import com.one.map.log.Logger;
import com.one.map.model.Address;
import com.one.map.view.IMapView;
import com.one.trip.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class SplashActivity extends AbsBaseActivity {

  private RelativeLayout mRootLayout;

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

  private ImageView mLogo;
  private ImageView mAd;
  private TextView mCountDown;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash_activity);
    SplashActivityPermissionsDispatcher.startLocationWithPermissionCheck(this);

    mRootLayout = findViewById(R.id.splash_root_layout);

    mLogo = (ImageView) findViewById(R.id.splash_logo);
    mAd = (ImageView) findViewById(R.id.splash_ad);
    mCountDown = (TextView) findViewById(R.id.splash_count_down);
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
    provider.buildLocation(this, IMapView.AMAP);
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

          ).setPositiveButton(R.string.one_action_settings,
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
    startAnim();
  }

  private void startAnim() {
    AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.splash_scale_anim);
//    AnimatorSet animator = new AnimatorSet();
//    ObjectAnimator alpha = ObjectAnimator.ofFloat(mRootLayout, "alpha", 1f, 0f);
//    ObjectAnimator scaleX = ObjectAnimator.ofFloat(mRootLayout, "scaleX", 1f, 2f);
//    ObjectAnimator scaleY = ObjectAnimator.ofFloat(mRootLayout, "scaleY", 1f, 2f);
//    animator.setDuration(3000);
//    alpha.addUpdateListener(new AnimatorUpdateListener() {
//      @Override
//      public void onAnimationUpdate(ValueAnimator animation) {
//        float percent = (float) animation.getAnimatedFraction();
//        Logger.e("ldx", "percent " + percent);
//        if (percent >= 0.30f) {
//          startMainActivity();
//        }
//      }
//    });
    animator.setTarget(mRootLayout);
    animator.setInterpolator(new LinearInterpolator());
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        startMainActivity();
      }

      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        finish();
      }
    });
//    animator.playTogether(alpha, scaleX, scaleY);
    animator.start();
  }

  private void startMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent);
  }

  @Override
  public void finish() {
    LocationProvider.getInstance().removeLocationChangedListener(locationChangedListener);
    super.finish();
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
