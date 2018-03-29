package com.test.demo.tts;

import android.content.Context;
import com.test.demo.utils.UIThreadHandler;
import java.util.List;

/**
 * 播放任务
 */
class PlayTask {

  /**
   * 播放状态
   */
  public enum PlayState {
    /**
     * 初始状态
     */
    STATE_IDLE(0x01),

    /**
     * 正在播放
     */
    STATE_PLAYING(0x02),

    /**
     * 正在播放语音
     */
    STATE_VOICE_PLAYING(0X03),

    /**
     * 播放完成
     */
    STATE_STOP(0x04);

    private final int mValue;

    PlayState(int state) {
      mValue = state;
    }

    public int getValue() {
      return mValue;
    }
  }

  /**
   * 解决语音播报空指针问题,随便起个名字,重构的时候优化掉
   */
  private Object synchronizedObj = new Object();
  /**
   * 该任务的播放状态
   */
  private PlayState mPlayState = PlayState.STATE_IDLE;
  /**
   * 等待一段数据播放完成的信号量
   */
  private Object mNextSegSig = new Object();
  /**
   * 是否正在等待语音数据下载完成
   */
  private volatile boolean mIsWaitingForVoice = false;
  /**
   * 当前播放的数据段的位置
   */
  private int mIndex = 0;
  /**
   * 播放语音等音频数据的播放器
   */
  private AudioPlayer mAudioPlayer;
  /**
   * 任务类型
   */
  private final Priority mPriority;
  /**
   * 播放数据
   */
  private final List<PlayData> mData;
  /**
   * 播放任务完成时通知DDPlayer的回调
   */
  private PlayTaskManager.TaskCompleteListener mTaskCompleteListener;
  /**
   * 监听播放状态变化的接口，通知UI层开始或停止播放动画
   */
  private final PlayStateListener mPlayStateListener;
  /**
   * Context
   */
  private final Context mContext;

  public PlayTask(final Context c, final Priority priority, final List<PlayData> data,
      final PlayStateListener listener) {
    if (data != null && !data.isEmpty()) {
      mPriority = priority;
      mData = data;
    } else {
      mPriority = Priority.INVALID;
      mData = null;
    }
    mContext = c;
    mPlayStateListener = listener;
    mAudioPlayer = new AudioPlayer(mSegmentCompleteListener);
  }

  /**
   * 获取播放任务的类型
   */
  public Priority getPriority() {
    return mPriority;
  }

  /**
   * 获取播放状态
   */
  public PlayState getPlayState() {
    return mPlayState;
  }

  /**
   * 更新播放任务的数据，只有订单会调用这个接口
   */
  public synchronized void updateTask(OrderUpdateData data) {
    synchronized (mNextSegSig) {
      if (data != null) {
        if (mPlayState == PlayState.STATE_IDLE) {
                    /* 该任务还没有开始播放 */
          if (data.mVoiceData != null) {
                        /* 要更新的是乘客的语音数据 */
            PlayData d = getVoiceData();
            if (d != null) {
              d.setVoice(data.mVoiceData);
            }
          } else {
                        /* 要更新的是小费调整信息 */
            if (mData != null && mData.size() >= 2) {
              mData.remove(1);
              mData.add(1, data.mPlayText);
            }
          }
        } else if (mPlayState == PlayState.STATE_PLAYING) {
                    /* 该任务已经开始播放，但语音数据尚未开始播放 */
          if (data.mVoiceData != null) {
                        /* 要更新的是乘客的语音数据 */
            PlayData d = getVoiceData();
            if (d != null) {
              d.setVoice(data.mVoiceData);
            }
          } else {
                        /* 要更新的是小费调整信息 */
            mData.add(2, data.mTipText);
          }

                    /* 播放线程如果正在等待语音数据，此时将其唤醒 */
          if (mIsWaitingForVoice) {
            mNextSegSig.notify();
          }
        } else if (mPlayState == PlayState.STATE_VOICE_PLAYING) {
                    /* 该任务的语音数据已经开始播放 */
          if (data.mVoiceData == null) {
                        /* 要更新的是小费调整信息 */
            mData.add(data.mTipText);
          }

                    /* 播放线程如果正在等待语音数据，此时将其唤醒 */
          if (mIsWaitingForVoice) {
            mNextSegSig.notify();
          }
        }
      } else {
                /* 播放线程如果正在等待语音数据，此时将其唤醒，因为语音下载失败了 */
        if (mIsWaitingForVoice) {
          mNextSegSig.notify();
        }
      }
    }
  }

  /**
   * 开始播放
   */
  public synchronized void play(final PlayTaskManager.TaskCompleteListener listener) {
    if (mPlayThread.getState() == Thread.State.NEW) {
      mPlayThread.start();
      mTaskCompleteListener = listener;
    }
  }

  /**
   * 停止播放
   */
  public synchronized void stop() {
    release();
//    SpeechSynthesizerHelper.getInstance().stopPlay();
  }

  private void release() {
    if (mAudioPlayer != null) {
      synchronized (synchronizedObj) {
        if (null != mAudioPlayer) {
          mAudioPlayer.stop();
          mAudioPlayer = null;
        }
      }
    }

    mPlayThread.setStopFlag(true);
    synchronized (mNextSegSig) {
      mNextSegSig.notify();
    }
  }

  /**
   * 获取乘客语音的数据段
   */
  private PlayData getVoiceData() {
    if (mData != null && mData.size() > 0) {
      for (PlayData d : mData) {
        if (d.getType() == PlayData.DataType.DATA_TYPE_BYTE) {
          return d;
        }
      }
    }
    return null;
  }

  /**
   * 一段数据播放完成
   */
  private final SegmentCompleteListener mSegmentCompleteListener = new SegmentCompleteListener() {

    @Override
    public void onSegmentComplete() {
      if (mIndex < mData.size()) {
        final PlayData preData = mData.get(mIndex);
        if (null != preData) {
          PlayData.DataType type = preData.getType();
          if (type == PlayData.DataType.DATA_TYPE_BYTE
              || type == PlayData.DataType.DATA_TYPE_FILE) {
            UIThreadHandler.post(onVoiceDataEnd);
          }
        }
      }

      if (mData.size() > mIndex + 1) {
        PlayData data = mData.get(mIndex + 1);
        if (data.getType() == PlayData.DataType.DATA_TYPE_BYTE
            || data.getType() == PlayData.DataType.DATA_TYPE_FILE) {
                    /* 如果还没有获取到音频数据时，等待到音频数据获取成功后，updateTask接口来唤醒线程继续播放 */
          if (data.getVoice() != null) {
            synchronized (mNextSegSig) {
              mNextSegSig.notify();
            }
          } else {
            synchronized (mNextSegSig) {
              mIsWaitingForVoice = true;
            }
          }
        } else {
          synchronized (mNextSegSig) {
            mNextSegSig.notify();
          }
        }
      } else {
        synchronized (mNextSegSig) {
          mNextSegSig.notify();
        }
      }
    }

    @Override
    public void onSegmentStart() {

    }

    @Override
    public void onSegmentError() {

    }
  };

  /**
   * 播放开始时通知UI线程
   *
   * @note 将在主线程中运行
   */
  private Runnable mPlayStart = new Runnable() {

    @Override
    public void run() {
      if (mPlayStateListener != null) {
        mPlayStateListener.onPlayStart(PlayTask.this.hashCode());
      }
    }
  };

  /**
   * 播放完成时通知UI线程
   *
   * @note 将在主线程中运行
   */
  private Runnable mPlayComplete = new Runnable() {

    @Override
    public void run() {
      if (mPlayStateListener != null) {
        mPlayStateListener.onPlayComplete(PlayTask.this.hashCode());
      }
    }
  };

  /**
   * 一段Voice数据播放开始时通知UI线程
   *
   * @note 将在主线程中运行
   */
  private Runnable onVoiceDataStart = new Runnable() {

    @Override
    public void run() {
      if (mPlayStateListener != null) {
        mPlayStateListener.onVoicePlayStateChanged(true);
      }
    }
  };

  /**
   * 一段Voice数据播放开始时通知UI线程
   *
   * @note 将在主线程中运行
   */
  private Runnable onVoiceDataEnd = new Runnable() {

    @Override
    public void run() {
      if (mPlayStateListener != null) {
        mPlayStateListener.onVoicePlayStateChanged(false);
      }
    }
  };

  class PlayTaskThread extends Thread {

    protected volatile boolean mStop = false;

    /**
     * 设置Stop值
     */
    public void setStopFlag(boolean isStop) {
      mStop = isStop;
    }
  }

  /**
   * 播放线程
   */
  private PlayTaskThread mPlayThread = new PlayTaskThread() {

    @Override
    public void run() {
            /* 设置stop为false */
      setStopFlag(false);

      mPlayState = PlayState.STATE_PLAYING;

      if (mPriority != Priority.INVALID && mData != null && mData.size() > 0) {
                /* 通知UI播放开始 */
        UIThreadHandler.post(mPlayStart);

        for (mIndex = 0; mIndex < mData.size(); mIndex++) {

          if (mStop) {
            break;
          }

          PlayData data = mData.get(mIndex);
          if (data == null) {
            continue;
          }
          // 初始化是否成功,如果不成功,会直接走回调,导致mNextSegSig对象锁出异常
          boolean result = true;
          if (data.getType() == PlayData.DataType.DATA_TYPE_TXT) {
//            result = SpeechSynthesizerHelper.getInstance().startPlay(data.getText(), mSegmentCompleteListener);
          } else if (data.getType() == PlayData.DataType.DATA_TYPE_RAW) {
            if (mAudioPlayer != null) {
              synchronized (synchronizedObj) {
                if (null != mAudioPlayer) {
                  mAudioPlayer.play(mContext, data.getRawId());
                }
              }
            }
          } else if (data.getType() == PlayData.DataType.DATA_TYPE_BYTE) {
            mPlayState = PlayState.STATE_VOICE_PLAYING;
            if (mAudioPlayer != null) {
              synchronized (synchronizedObj) {
                if (null != mAudioPlayer) {
                  UIThreadHandler.post(onVoiceDataStart);
                  mAudioPlayer.play(mContext, data.getVoice());
                }
              }
            }
          } else if (data.getType() == PlayData.DataType.DATA_TYPE_FILE) {
            mPlayState = PlayState.STATE_VOICE_PLAYING;
            if (mAudioPlayer != null) {
              synchronized (synchronizedObj) {
                if (null != mAudioPlayer) {
                  UIThreadHandler.post(onVoiceDataStart);
                  mAudioPlayer.play(mContext, data.getFile().getAbsolutePath());
                }
              }
            }
          }

          if (result) {
                    /* 等待当前数据段播放完成 */
            synchronized (mNextSegSig) {
              try {
                mNextSegSig.wait();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
        }

        if (!mStop) {
                    /* 释放Player资源 */
          PlayTask.this.release();
        }

                /* 通知UI层播放任务结束 */
        UIThreadHandler.post(mPlayComplete);

                /* 通知播放器，该播放任务完成，可以开始下一播放任务了 */
        if (mTaskCompleteListener != null) {
          mTaskCompleteListener.onPlayTaskComplete(PlayTask.this.hashCode());
          mTaskCompleteListener = null;
        }
      }

      mPlayState = PlayState.STATE_STOP;
    }
  };

  /**
   * 播放任务中的一段数据播放完成时的回调 一个订单的播放任务，可能由raw,TTS以及voice几部分组成 每部分会依次播放，当一部分播放完成时会调用该接口
   */
  public interface SegmentCompleteListener {

    /**
     * 一段数据播放完成
     */
    void onSegmentComplete();

    /**
     * 开始播放
     */
    void onSegmentStart();

    /**
     * 播放错误
     */
    void onSegmentError();

  }

}
