package com.test.demo.tts;

class OrderUpdateData {

  /**
   * 订单播报的完整的TTS文本信息
   */
  public PlayData mPlayText = null;

  /**
   * 订单小费信息发生变化时，变化后的小费信息
   */
  public PlayData mTipText = null;

  /**
   * 语音数据
   */
  public byte[] mVoiceData = null;
}
