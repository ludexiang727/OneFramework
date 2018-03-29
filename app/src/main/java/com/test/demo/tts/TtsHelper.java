package com.test.demo.tts;


import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 语音播放辅助类
 */
public class TtsHelper {

  private TtsHelper() {
  }

  /**
   * 把语音播放任务加到队列，按优先级播放
   */
  public static int play(final Context context, final String txt) {
    return play(context, txt, Priority.PUSH_MSG);//默认优先级
  }

  public static int play(final Context context, final String txt, final Priority priority) {
    if (!TextUtils.isEmpty(txt)) {
      final ArrayList<PlayData> arr = new ArrayList<>();
      arr.add(new PlayData(txt));
      return play(new PlayTask(context, priority, arr, null));
    }
    return -1;
  }

  public static int play(final Context context, final int rawId) {
    return play(context, rawId, Priority.PUSH_MSG);
  }

  public static int play(final Context context, final int rawId, final Priority priority) {
    final ArrayList<PlayData> arr = new ArrayList<>();
    arr.add(new PlayData(rawId));
    return play(new PlayTask(context, priority, arr, null));
  }

  public static int play(final Context context, final byte[] tts) {
    return play(context, tts, null);
  }

  public static int play(final Context context, final byte[] tts,
      final PlayStateListener listener) {
    return play(context, tts, Priority.PUSH_MSG, listener);
  }

  public static int play(final Context context, final byte[] tts, final Priority priority,
      final PlayStateListener listener) {
    if (null != tts) {
      final ArrayList<PlayData> arr = new ArrayList<>();
      arr.add(new PlayData(tts));

      final PlayTask task = new PlayTask(context, priority, arr, listener);
      return PlayTaskManager.getInstance().play(task);
    }

    return -1;
  }

  public static int playList(final Context context, final List<PlayData> tts) {
    return playList(context, tts, null);
  }

  public static int playList(final Context context, final List<PlayData> tts,
      final PlayStateListener listener) {
    return playList(context, tts, Priority.PUSH_MSG, listener);
  }

  public static int playList(final Context context, final List<PlayData> tts,
      final Priority priority,
      final PlayStateListener listener) {
    if (null != tts && !tts.isEmpty()) {
      PlayTask task = new PlayTask(context, priority, tts, listener);
      return PlayTaskManager.getInstance().play(task);
    }

    return -1;
  }

  /**
   * 强制播放当前语音，停止其他播放任务
   */
  public static void manualPlay(final Context context, final String txt) {
    manualPlay(context, txt, null);//默认优先级
  }

  public static int manualPlay(final Context context, final String txt,
      final PlayStateListener listener) {
    if (!TextUtils.isEmpty(txt)) {
      final ArrayList<PlayData> arr = new ArrayList<>();
      arr.add(new PlayData(txt));

      final PlayTask task = new PlayTask(context, Priority.PUSH_MSG, arr,
          listener);
      return manualPlay(task);
    }

    return -1;
  }

  public static void manualPlay(final Context context, final String txt, final Priority priority,
      final PlayStateListener listener) {
    if (!TextUtils.isEmpty(txt)) {
      final ArrayList<PlayData> arr = new ArrayList<>();
      arr.add(new PlayData(txt));
      final PlayTask task = new PlayTask(context, priority, arr, listener);
      manualPlay(task);
    }
  }

  public static void manualPlay(final Context context, final int rawId) {
    manualPlay(context, rawId, Priority.PUSH_MSG);
  }

  public static void manualPlay(final Context context, final int rawId, final Priority priority) {
    final ArrayList<PlayData> arr = new ArrayList<>();
    arr.add(new PlayData(rawId));
    final PlayTask task = new PlayTask(context, priority, arr, null);
    manualPlay(task);
  }

  public static int manualPlay(final Context context, final byte[] tts) {
    return manualPlay(context, tts, null);
  }

  public static int manualPlay(final Context context, final byte[] tts,
      final PlayStateListener listener) {
    int taskId = -1;
    if (null != tts) {
      final ArrayList<PlayData> arr = new ArrayList<>();
      arr.add(new PlayData(tts));

      final PlayTask task = new PlayTask(context, Priority.PUSH_MSG, arr,
          listener);
      taskId = PlayTaskManager.getInstance().manualPlay(task);
    }
    return taskId;
  }

  public static int manualPlay(Context context, File tts) {
    return manualPlay(context, tts, null);
  }

  public static int manualPlay(final Context context, final File tts,
      final PlayStateListener listener) {
    if (null != tts) {
      final ArrayList<PlayData> arr = new ArrayList<>();
      arr.add(new PlayData(tts));

      final PlayTask task = new PlayTask(context, Priority.PUSH_MSG, arr,
          listener);
      return PlayTaskManager.getInstance().manualPlay(task);
    }

    return -1;
  }

  public static int play(final Context context, final List<String> txtList) {
    return play(context, txtList, Priority.PUSH_MSG);//默认优先级
  }

  public static int play(final Context context, final List<String> txtList,
      final Priority priority) {
    if (txtList != null && txtList.size() > 0) {
      final ArrayList<PlayData> arr = new ArrayList<>();
      for (final String txt : txtList) {
        arr.add(new PlayData(txt));
      }

      final PlayTask task = new PlayTask(context, priority, arr, null);
      return PlayTaskManager.getInstance().play(task);
    }

    return 0;
  }

  public static void stop(final int id) {
    PlayTaskManager.getInstance().stop(id);
  }

  public static void stopAll() {
    PlayTaskManager.getInstance().stopAll();
  }

  private static int manualPlay(final PlayTask task) {
    return PlayTaskManager.getInstance().manualPlay(task);
  }

  private static int play(final PlayTask task) {
    return PlayTaskManager.getInstance().play(task);
  }

  public static void release() {
//    SpeechSynthesizerHelper.getInstance().release();
  }

  public static void initEngine(final Context context, String appId, String apiKey,
      String secretKey) {
//    SpeechSynthesizerHelper.getInstance().initEngine(context, appId, apiKey, secretKey);
  }

}
