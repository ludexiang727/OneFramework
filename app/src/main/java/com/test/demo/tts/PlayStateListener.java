package com.test.demo.tts;

/**
 * 通知UI播放任务开始和停止的接口
 */
public interface PlayStateListener {

  /**
   * 通知UI，播放任务开始了，可以开始展示播放动画了
   *
   * @param taskId 播放任务的id
   */
  void onPlayStart(int taskId);

  /**
   * 通知UI，播放任务结束了，可以停止播放动画了
   *
   * @param taskId 播放任务的id
   */
  void onPlayComplete(int taskId);

  void onVoicePlayStateChanged(boolean start);

}
