package com.test.demo.tts;

import java.io.File;

public class PlayData {

  /**
   * 数据类型
   */
  public enum DataType {
    /**
     * 需要TTS播放的文本
     */
    DATA_TYPE_TXT(0x01),

    /**
     * 内置的音频文件
     */
    DATA_TYPE_RAW(0x02),

    /**
     * 语音的字节数据
     */
    DATA_TYPE_BYTE(0x03),

    /**
     * 下载的语音文件
     */
    DATA_TYPE_FILE(0x04),;

    private int mValue;

    DataType(int type) {
      mValue = type;
    }

    public int getValue() {
      return mValue;
    }
  }

  /**
   * 数据类型
   */
  private DataType mType;

  /**
   * 内置音频文件的id，对应DATA_TYPE_RAW
   */
  private int mRawId;

  /**
   * TTS播放的文本,对应DATA_TYPE_TXT
   */
  private String mText = null;

  /**
   * 语音数据的字节数组,对应DATA_TYPE_BYTE
   */
  private byte[] mVoice = null;

  /**
   * 下载的语音文件,对应DATA_TYPE_FILE
   */
  private File mFile = null;

  /**
   * 用内置的raw数据创建PlayData
   */
  public PlayData(int rawId) {
    mType = DataType.DATA_TYPE_RAW;
    mRawId = rawId;
  }

  /**
   * 用要播放的TTS文本创建PlayData
   */
  public PlayData(String text) {
    mType = DataType.DATA_TYPE_TXT;
    mText = text;
  }

  /**
   * 用要播放的语音的字节数据创建PlayData
   */
  public PlayData(byte[] voice) {
    mType = DataType.DATA_TYPE_BYTE;
    mVoice = voice;
  }

  /**
   * 用下载的语音文件创建PlayData
   */
  public PlayData(File file) {
    mType = DataType.DATA_TYPE_FILE;
    mFile = file;
  }

  /**
   * 创建一个指定类型的空数据，用于占位，主要用于byte和file类型
   */
  public PlayData(DataType type) {
    mType = type;
  }

  /**
   * 获取数据类型
   */
  public DataType getType() {
    return mType;
  }

  /**
   * 获取要播放的资源文件id
   */
  public int getRawId() {
    return mRawId;
  }

  /**
   * 获取要播放的Text文本
   */
  public String getText() {
    return mText;
  }

  /**
   * 获取要播放的语音的字节数据
   */
  public byte[] getVoice() {
    return mVoice;
  }

  /**
   * 设置要播放的预约的字节数据
   */
  public void setVoice(byte[] voice) {
    mVoice = voice;
  }

  /**
   * 获取要播放的语音文件
   */
  public File getFile() {
    return mFile;
  }

  /**
   * 设置要播放的语音文件
   */
  public void setFile(File f) {
    mFile = f;
  }

}
