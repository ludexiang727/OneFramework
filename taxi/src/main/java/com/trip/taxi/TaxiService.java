package com.trip.taxi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.one.framework.app.login.UserProfile;
import com.one.framework.net.base.BaseObject;
import com.one.framework.net.response.IResponseListener;
import com.one.map.location.LocationProvider;
import com.one.map.location.LocationProvider.OnLocationChangedListener;
import com.one.map.log.Logger;
import com.one.map.model.Address;
import com.trip.base.common.CommonParams;
import com.trip.taxi.net.TaxiRequest;
import com.trip.taxi.net.model.TaxiOrderDriverLocation;
import com.trip.taxi.net.model.TaxiOrderStatus;

/**
 * Created by ludexiang on 2018/6/14.
 * 1. 轮询订单状态
 * 2. 上报位置
 */

public class TaxiService extends Service {

  private static final String TAG = TaxiService.class.getSimpleName();

  private static final String COMMAND_KEY = "_command"; // 启动该服务的时候传输的一些命令
  private static final String COMMAND_ISSTART = "start";
  private static final String COMMAND_ORDERID = "orderId";

  private static final int KEY_COMMAND_REPORT_LOCATION = 0;
  private static final int KEY_COMMAND_REPORT_TRACK = 1;
  private static final int KEY_COMMAND_ORDER_STATUS = 2;

  private static final int REPORT_LOCATION = 4000;
  private static final int LOOP_ORDER_STATUS = 2000;

  private LocalBroadcastManager mBroadManager;

  private static final int TRACK_DURING = 60000;
  private HandlerThread mHandlerThread;
  private Handler mHandler;

  private static boolean isLooperOrderStatus = false;

  private static boolean isStopService = false;

//  private Handler trackHandler = new Handler(Looper.getMainLooper());

  public static void stopService() {
    Logger.e(TAG, "Service Handler >>>> " + isStopService);
    isStopService = true;
    isLooperOrderStatus = false;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Logger.e(TAG, "Service  onCreate ....");
    mBroadManager = LocalBroadcastManager.getInstance(getApplicationContext());
    mHandlerThread = new HandlerThread("SERVICE_THREAD");
    mHandlerThread.start();
    mHandler = new Handler(mHandlerThread.getLooper()) {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Logger.e(TAG, "Service Handler >>>> " + isStopService + " service " + this);
        if (isStopService) {
          mHandlerThread.quit();
          stopSelf();
          return;
        }
        switch (msg.what) {
          case KEY_COMMAND_REPORT_LOCATION: {
            startReportLocation();
            break;
          }
          case KEY_COMMAND_ORDER_STATUS: {
            String orderId = (String) msg.obj;
            startLoopOrderStatus(orderId);
            startLoopDriverLocation(orderId);
            break;
          }
        }
      }
    };
//    reportTask = new ReportTask();
  }

  private static void start(Intent baseIntent, Context ctx, int command, boolean isStart) {
    isStopService = false;
    Intent serviceIntent = new Intent(baseIntent);
    serviceIntent.setClass(ctx, TaxiService.class);
    serviceIntent.putExtra(COMMAND_KEY, command);
    serviceIntent.putExtra(COMMAND_ISSTART, isStart);
    ctx.startService(serviceIntent);
  }

  /**
   * 上传经纬度
   */
  public static void reportLocation(Context context, boolean isStart) {
    start(new Intent(), context, KEY_COMMAND_REPORT_LOCATION, isStart);
  }

  public static void reportTripTrack(Context context, boolean isStart, String orderId) {
    Intent intent = new Intent();
    intent.putExtra(COMMAND_ORDERID, orderId);
    start(intent, context, KEY_COMMAND_REPORT_TRACK, isStart);
  }

  /**
   * 轮询订单状态
   * @param context
   * @param isStart
   * @param orderId
   */
  public static void loopOrderStatus(Context context, boolean isStart, String orderId) {
    if (!isLooperOrderStatus) {
      Intent intent = new Intent();
      intent.putExtra(COMMAND_ORDERID, orderId);
      start(intent, context, KEY_COMMAND_ORDER_STATUS, isStart);
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      int command = intent.getIntExtra(COMMAND_KEY, 0);
      boolean isStart = intent.getBooleanExtra(COMMAND_ISSTART, true);
      switch (command) {
        case KEY_COMMAND_REPORT_LOCATION: {
          if (isStart) {
            mHandler.removeMessages(KEY_COMMAND_REPORT_LOCATION);
            mHandler.sendEmptyMessage(KEY_COMMAND_REPORT_LOCATION);
          } else {
            mHandler.removeMessages(KEY_COMMAND_REPORT_LOCATION);
          }
          break;
        }
        case KEY_COMMAND_REPORT_TRACK: {
          String orderId = intent.getStringExtra(COMMAND_ORDERID);
//          reportTrack(isStart, orderId);
          break;
        }
        case KEY_COMMAND_ORDER_STATUS: {
          String orderId = intent.getStringExtra(COMMAND_ORDERID);
          mHandler.removeMessages(KEY_COMMAND_ORDER_STATUS);
          Message message = mHandler.obtainMessage(KEY_COMMAND_ORDER_STATUS);
          message.obj = orderId;
          message.sendToTarget();
          break;
        }
        default:
          break;
      }
    }
    return super.onStartCommand(intent, flags, startId);
  }


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  /**
   * report location
   */
  private void startReportLocation() {
    Address address = LocationProvider.getInstance().getLocation();
    if (address != null) {
      TaxiRequest.taxiReportLocation("", address, new IResponseListener<BaseObject>() {
        @Override
        public void onSuccess(BaseObject baseObject) {

        }

        @Override
        public void onFail(int errCode, String message) {
        }

        @Override
        public void onFinish(BaseObject baseObject) {
          mHandler.removeMessages(KEY_COMMAND_REPORT_LOCATION);
          mHandler.sendEmptyMessageDelayed(KEY_COMMAND_REPORT_LOCATION, REPORT_LOCATION);
        }
      });
    } else {
      mHandler.removeMessages(KEY_COMMAND_REPORT_LOCATION);
      mHandler.sendEmptyMessageDelayed(KEY_COMMAND_REPORT_LOCATION, REPORT_LOCATION);
    }
  }

  /**
   * 处理上报轨迹的逻辑
   */
//  private void reportTrack(boolean isStart, String orderID) {
//    isTrackStart = isStart;
//    if (isStart) {
//      LocationProvider.getInstance().removeLocationChangedListener(mDriverLocationListener);
//      LocationProvider.getInstance().addLocationChangeListener(mDriverLocationListener);
//      trackOrder = orderID;
//      trackHandler.removeCallbacksAndMessages(null);
//      trackHandler.postDelayed(trackRunnable, TRACK_DURING);
//
//    } else {
//      LocationProvider.getInstance().removeLocationChangedListener(mDriverLocationListener);
//      trackHandler.post(trackRunnable);
//    }
//  }

  private String trackOrder = "";
  private StringBuffer reportInfo = new StringBuffer();
  private String reportingInfo;
  private boolean isTrackStart = false;

  private void buildTrack(Address address) {
    if (!TextUtils.isEmpty(reportInfo)) {
      reportInfo.append("#");
    }
    reportInfo.append(address.mAdrLatLng.longitude);
    reportInfo.append(",");
    reportInfo.append(address.mAdrLatLng.latitude);
    reportInfo.append(",");
    reportInfo.append(System.currentTimeMillis());
    reportInfo.append(",");
    reportInfo.append(address.speed);
    reportInfo.append(",");
    reportInfo.append(address.bearing);
    reportInfo.append(",");
    reportInfo.append(address.accuracy);
  }

//  private Runnable trackRunnable = new Runnable() {
//    @Override
//    public void run() {
//      if (!TextUtils.isEmpty(reportInfo.toString())) {
////        reportTask.reportTripTrack(trackOrder, reportInfo.toString()).subscribe(
////            new Observer<ApiResult<Object>>() {
////              @Override
////              public void onSubscribe(Disposable d) {
////
////              }
////
////              @Override
////              public void onNext(ApiResult<Object> objectApiResult) {
////                reportingInfo = "";
////                if (isTrackStart) {
////                  trackHandler.removeCallbacksAndMessages(null);
////                  trackHandler.postDelayed(trackRunnable, TRACK_DURING);
////                }
////
////              }
////
////              @Override
////              public void onError(Throwable e) {
////                reportInfo.append(reportingInfo);
////                reportingInfo = "";
////                if (isTrackStart) {
////                  trackHandler.removeCallbacksAndMessages(null);
////                  trackHandler.postDelayed(trackRunnable, TRACK_DURING);
////                }
////              }
////
////              @Override
////              public void onComplete() {
////
////              }
////            });
//        reportingInfo = reportInfo.toString();
//        int length = reportInfo.length();
//        reportInfo.delete(0, length);
//      } else {
//        if (isTrackStart) {
//          trackHandler.removeCallbacksAndMessages(null);
//          trackHandler.postDelayed(trackRunnable, TRACK_DURING);
//        }
//      }
//    }
//  };


  /**
   * 轮询订单状态
   */
  private void startLoopOrderStatus(final String oid) {
    isLooperOrderStatus = true;
    TaxiRequest.taxiLoopOrderStatus(UserProfile.getInstance(this).getUserId(), oid, new IResponseListener<TaxiOrderStatus>() {
      @Override
      public void onSuccess(TaxiOrderStatus taxiOrderStatus) {
        Intent intent = new Intent(CommonParams.COMMON_LOOPER_ORDER_STATUS);
        intent.putExtra(CommonParams.COMMON_LOOPER_ORDER, taxiOrderStatus);
        mBroadManager.sendBroadcast(intent);
      }

      @Override
      public void onFail(int errCode, String message) {

      }

      @Override
      public void onFinish(TaxiOrderStatus taxiOrderStatus) {
        mHandler.removeMessages(KEY_COMMAND_ORDER_STATUS);
        Message message = mHandler.obtainMessage(KEY_COMMAND_ORDER_STATUS);
        message.obj = oid;
        mHandler.sendMessageDelayed(message, LOOP_ORDER_STATUS);
      }
    });
  }

  /**
   * 轮询司机位置
   */
  private void startLoopDriverLocation(String oid) {
    TaxiRequest.taxiDriverLocation(UserProfile.getInstance(this).getUserId(), oid,
        new IResponseListener<TaxiOrderDriverLocation>() {
          @Override
          public void onSuccess(TaxiOrderDriverLocation taxiOrderDriverLocation) {
            Intent intent = new Intent(CommonParams.COMMON_LOOPER_DRIVER_LOCATION);
            intent.putExtra(CommonParams.COMMON_LOOPER_DRIVER, taxiOrderDriverLocation);
            mBroadManager.sendBroadcast(intent);
          }

          @Override
          public void onFail(int errCode, String message) {

          }

          @Override
          public void onFinish(TaxiOrderDriverLocation taxiOrderDriverLocation) {

          }
        });
  }

  @Override
  public void onDestroy() {
    mHandler.removeCallbacksAndMessages(null);
    LocationProvider.getInstance().removeLocationChangedListener(mDriverLocationListener);
    super.onDestroy();
  }


  private OnLocationChangedListener mDriverLocationListener = new OnLocationChangedListener() {
    @Override
    public void onLocationChanged(Address location) {
      if (location != null) {
        buildTrack(location);
      }
    }
  };
}
