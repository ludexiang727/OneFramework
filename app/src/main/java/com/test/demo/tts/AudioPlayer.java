package com.test.demo.tts;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 播放raw中的资源文件以及下载的语音文件
 */
class AudioPlayer {

  private static final String TAG = "AudioPlayer";

  private MediaPlayer mMediaPlayer;
  /**
   * 播放完毕时的回调
   */
  private PlayTask.SegmentCompleteListener mSegmentCompleteListener;

  /**
   * 创建播放器对象
   *
   * @param listener 播放完成时的回调
   */
  public AudioPlayer(PlayTask.SegmentCompleteListener listener) {
    mSegmentCompleteListener = listener;
  }

  /**
   * 播放raw资源
   *
   * @param rawId 资源id
   */
  public synchronized void play(Context c, int rawId) {
    try {
      mMediaPlayer = MediaPlayer.create(c, rawId);
    } catch (final NullPointerException e) {
    }

    if (null != mMediaPlayer) {
      synchronized (mMediaPlayer) {
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);

        try {
          mMediaPlayer.start();
        } catch (final IllegalStateException e) {
        }
      }
    }
  }

  /**
   * 播放语音文件
   *
   * @param path 语音文件路径
   * @note 一定不能在主线程中调用
   */
  public synchronized void play(Context c, String path) {
    if (TextUtils.isEmpty(path)) {
      if (mSegmentCompleteListener != null) {
        mSegmentCompleteListener.onSegmentComplete();
      }
      return;
    }

    mMediaPlayer = new MediaPlayer();
    synchronized (mMediaPlayer) {
      mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
      FileInputStream fis = null;
      try {
        File file = new File(path);
        fis = new FileInputStream(file);
        FileDescriptor fd = fis.getFD();
        mMediaPlayer.setDataSource(fd);
        mMediaPlayer.prepare();
      } catch (final Exception e) {
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException e) {
          }
        }
      }
    }

    try {
      if (mMediaPlayer != null) {
        synchronized (mMediaPlayer) {
          mMediaPlayer.start();
        }
      }
    } catch (final IllegalStateException e) {
      if (mSegmentCompleteListener != null) {
        mSegmentCompleteListener.onSegmentComplete();
      }
    }
  }

  /**
   * 播放音频字节数据
   *
   * @param voice 音频字节数据
   * @note 一定不能在主线程中调用
   */
  public synchronized void play(Context c, byte[] voice) {
    try {
      if (voice != null && c != null) {
        String fileName = "temp";
        c.deleteFile(fileName);
        FileOutputStream out = c.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
        out.write(voice);
        out.flush();
        out.close();

        File f = c.getFileStreamPath(fileName);
        play(c, f.getAbsolutePath());
      } else {
        play(c, "");
      }
    } catch (final IOException e) {
      if (mSegmentCompleteListener != null) {
        mSegmentCompleteListener.onSegmentComplete();
      }
    }
  }

  /**
   * 停止播放
   */
  public synchronized void stop() {
    if (mMediaPlayer != null) {
      synchronized (mMediaPlayer) {
        try {
          mMediaPlayer.stop();
        } catch (final IllegalStateException e) {
//                    LogService.getInstance().e(TAG, e.getLocalizedMessage(), e);
        } finally {
                    /* 若手动终止播放，则不会回调onCompleteListener，需手动触发下 mSegmentCompleteListener */
          if (mSegmentCompleteListener != null) {
            mSegmentCompleteListener.onSegmentComplete();
          }
          mMediaPlayer.release();
          mMediaPlayer = null;
        }
      }
    }
  }

  /**
   * 完成/出错时的监听接口
   */
  private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {

    @Override
    public void onCompletion(MediaPlayer mp) {
      if (mMediaPlayer != null && mp != null && mMediaPlayer == mp) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
      } else {
        if (null != mp) {
          mp.release();
        }
      }

      if (mSegmentCompleteListener != null) {
        mSegmentCompleteListener.onSegmentComplete();
      }
    }

  };
}
