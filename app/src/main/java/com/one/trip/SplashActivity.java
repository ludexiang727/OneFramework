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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.one.framework.MainActivity;
import com.one.framework.app.base.AbsBaseActivity;
import com.one.framework.app.widget.ShapeImageView;
import com.one.framework.dialog.SupportDialogFragment;
import com.one.framework.utils.UIThreadHandler;
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

  private int DOWN_TIME = 10;

  private RelativeLayout mRootLayout;

  interface DialogClickListener {

    void onClickOk();

    void onClickCancel();
  }

  private boolean isNeedResumePermission;
  private boolean requestGettingOperationConfig = false;
  private AlertDialog mAlertDialog, errorCodeDialog;
  private SupportDialogFragment mPermissionDialog;
  private final int showErrorTimes = 5;
  private volatile int errorCount = 0;
  private boolean haveDoNext = false;

  private ImageView mLogo;
  private ShapeImageView mAd;
  private TextView mCountDown;

  private HandlerThread mDownHandler;
  private Handler mHandler;
  private byte[] lock = new byte[0];

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash_activity);
    SplashActivityPermissionsDispatcher.startLocationWithPermissionCheck(this);

    mRootLayout = findViewById(R.id.splash_root_layout);

    mLogo = (ImageView) findViewById(R.id.splash_logo);
    mAd = (ShapeImageView) findViewById(R.id.splash_ad);
    mCountDown = (TextView) findViewById(R.id.splash_count_down);
    mCountDown.setText(String.format(getString(R.string.splash_skip_second), DOWN_TIME));
    mDownHandler = new HandlerThread("COUNT_DOWN");
    mDownHandler.start();
    mHandler = new Handler(mDownHandler.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case 0: {
            UIThreadHandler.post(new Runnable() {
              @Override
              public void run() {
                if (DOWN_TIME > 0) {
                  mCountDown.setText(String.format(getString(R.string.splash_skip_second), DOWN_TIME));
                }
                if (DOWN_TIME == 0) {
                  finishActivity();
                }
              }
            });
            break;
          }
          case 1: {
            countDown();
            break;
          }
        }
      }
    };
    mCountDown.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        DOWN_TIME = -1;
        if (mDownHandler != null) {
          mDownHandler.quit();
        }
        startMainActivity();
        finish();
      }
    });
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
    mHandler.sendEmptyMessage(1);
  }

  private void showHintDialog(int message) {
    if (mPermissionDialog == null || !mPermissionDialog.isHidden()) {
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
//    finishActivity();
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

    SupportDialogFragment.Builder builder = new SupportDialogFragment.Builder(this)
        .setTitle("")
        .setMessage(getString(message))
        .setPositiveButton(getString(android.R.string.cancel), new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mPermissionDialog.dismiss();
            dialogClickListener.onClickCancel();
          }
        })
        .setPositiveButtonTextColor(Color.parseColor("#A3D2E4"))
        .setNegativeButton(getString(android.R.string.ok), new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mPermissionDialog.dismiss();
            dialogClickListener.onClickOk();
          }
        });
    mPermissionDialog = builder.create();
    mPermissionDialog.setCancelable(false);
    mPermissionDialog.show(getSupportFragmentManager(), "");
    requestGettingOperationConfig = false;
  }

  private void finishActivity() {
    startAnim();
  }

  private void startAnim() {
    AnimatorSet animator = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.splash_scale_anim);
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

  private void countDown() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (DOWN_TIME > 0) {
          synchronized (lock) {
            try {
              lock.wait(1000); // lock的是new Thread 而不是 mDownHandler的线程
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
          mHandler.sendEmptyMessage(0);
          DOWN_TIME--;
        }
      }
    }).start();
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
