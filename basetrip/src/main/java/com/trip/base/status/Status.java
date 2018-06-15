package com.trip.base.status;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ludexiang on 2018/1/8.
 * 包含行程状态及支付状态
 */

public class Status {

  // Implementing a fromString method on an enum type
  private static final Map<String, OrderStatus> orderToEnum = new HashMap<String, OrderStatus>();

  static {
    // Initialize map from constant name to enum constant
    for (OrderStatus status : OrderStatus.values()) {
      orderToEnum.put(status.toString(), status);
    }
  }

  // Returns Status for string, or null if string is invalid
  public static OrderStatus getOrderStatus(String symbol) {
    if (orderToEnum.containsKey(symbol)) {
      return orderToEnum.get(symbol);
    }
    return OrderStatus.UNKNOW;
  }

  /**
   * 订单状态
   */
  public enum OrderStatus implements Serializable {
    UNKNOW(-1),  // 返回的状态 OrderStatus 不包含则不做处理
    CREATE(0),  //新创建的订单，司机尚未接单
    RECEIVED(1), //司机接单
    SETOFF(2), //司机出发，前往乘客上车地点
    READY(3), //司机到达乘客上车地点
    START(4), // 接到乘客，行程开始
    ARRIVED(5), //到达终点
    REASSIGN(6),  // 重新派单
    CONFIRM(7), //顺风车确认搭乘（终态）
    CANCELED(8), //订单取消（终态）
    PAID(9),  // 支付完成（终态)
    UNPAID(10), //未支付（到达终点或者取消需要支付取消费用）
    CONFIRMEDPRICE(11), //确认价格
    AUTOPAY(12),  // 发起支付
    AUTOPAID(13), // 已自动支付，费用未结清，待手动支付
    REFUND(14),  // 退款中
    REFUNDED(15),  // 退款完成（终态）
    ABSENCE(16), // 订单不存在
    CLOSE(17),  //交易关闭（终态），如取消不需要支付费用
    CANCELED_AUTOPAID(18), // 取消待支付
    CONFIRMED_PRICE(19), // 等待乘客确认金额
    CANCELED_PAID(20), // 乘客取消已支付
    AUTOPAYING(21),//支付中，乘客正在扣款
    COMPLAINT(22);

    int mValue;

    OrderStatus(int value) {
      mValue = value;
    }

    public int getValue() {
      return mValue;
    }
  }
}


