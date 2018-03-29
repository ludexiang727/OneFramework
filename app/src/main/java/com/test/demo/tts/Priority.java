package com.test.demo.tts;

public enum Priority {

  /**
   * 无效类型
   */
  INVALID(0x00),

  /**
   * 导航语音
   */
  NAVI(0x01),

  /**
   * 推送消息
   */
  PUSH_MSG(0x02),

  /**
   * 订单
   */
  ORDER(0x03),

  /**
   * 推送消息-高优先级
   */
  PUSH_MSG_HP(0x04),;

  private final int mValue;

  Priority(final int type) {
    this.mValue = type;
  }

  public int getValue() {
    return this.mValue;
  }

}
