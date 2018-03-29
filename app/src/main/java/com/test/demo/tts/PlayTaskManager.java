package com.test.demo.tts;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TTS以及语音播放类，单例
 */
class PlayTaskManager {

  private static final class Singleton {

    private static final PlayTaskManager INSTANCE = new PlayTaskManager();
  }

  /**
   * 等待播放的订单任务
   */
  private volatile List<PlayTask> mOrder = new ArrayList<PlayTask>();
  /**
   * 等待播报的Push任务
   */
  private volatile List<PlayTask> mMsg = new ArrayList<PlayTask>();

  /**
   * 当前的播放任务
   */
  private volatile PlayTask mCurTask = null;
  /**
   * mCurTask的同步锁
   */
  private static final ReentrantLock LOCK = new ReentrantLock();

  PlayTaskManager() {
  }

  /**
   * 获取播放器实例
   */
  public static synchronized PlayTaskManager getInstance() {
    return Singleton.INSTANCE;
  }

  /**
   * 播放任务
   *
   * @return 播报任务的id，-1表示创建播放任务失败
   */
  public synchronized int play(PlayTask task) {
    if (task == null || task.getPriority() == Priority.INVALID) {
      return -1;
    }

    LOCK.lock();
    if (mCurTask != null && mCurTask.getPriority() != Priority.INVALID) {
      /* 将新的播放任务添加到任务列表 */
      switch (task.getPriority()) {
        case ORDER: // 订单
        case PUSH_MSG: // Push消息
        case PUSH_MSG_HP: // Push紧急消息
          mMsg.add(task);
          break;

        case NAVI:
        case INVALID:
        default:
          LOCK.unlock();
          return -1;
      }
            
      /* 有当前正在播放的任务 */
      if (mCurTask.getPriority().getValue() < task.getPriority().getValue()) {
      /*
       * 当前的播放任务优先级低于正要播放的任务
       * 停掉当前任务，当前任务停止后，会自动开始新任务(startNextTask)
       */
        mCurTask.stop();
      }

    } else {
            /* 无当前正在播放的任务，直接开始播放 */
      mCurTask = task;
      mCurTask.play(mTaskCompleteListener);
    }
    LOCK.unlock();

    return task.hashCode();
  }

  /**
   * 手动点击播放。不管当前播放的内容是什么，先停止当前的播放，开始播放新的内容
   *
   * @param task 要播放的数据
   */
  public synchronized int manualPlay(PlayTask task) {
    if (task == null || task.getPriority() == Priority.INVALID) {
      return -1;
    }

    LOCK.lock();
    if (mCurTask != null) {
      /*
       * 有当前正在播放的任务
       * 先停掉当前正在播放的任务，立即开始播放新任务
       */
      mCurTask.stop();
    }

    mCurTask = task;
    mCurTask.play(mTaskCompleteListener);
    LOCK.unlock();

    return task.hashCode();
  }

  /**
   * 订单播放任务临时有小费信息变化时或乘客语音下载完成时调用的接口
   * 小费信息有变化时
   * 如果之前的播放任务尚未完成，则会合并到之前的任务中一起播放
   * 如果之前的播放任务已经完成，则会创建一个新的播放任务来播报变化后的小费信息
   *
   * @param id 该订单的播放任务id
   * @param data 变化后的播报数据
   * @param c 创建新的播放任务时会用到
   * @param listener 如果该订单的播放任务已完成，则附加信息的播放状态会通过该接口回调 如果该订单的播放任务尚未完成，则播放状态会通过之前的订单播放任务的接口回调
   */
  public synchronized void updateTask(int id, OrderUpdateData data, Context c,
      PlayStateListener listener) {
    LOCK.lock();
    if (mCurTask == null) {
            /*
             * 没有当前正在播放的任务
             * 可能当前任务已经播放完，需要单独播放小费信息
             */
      if (data != null && data.mTipText != null) {
                /* 更新小费信息 */
        ArrayList<PlayData> arr = new ArrayList<PlayData>();
        arr.add(data.mTipText);
        mCurTask = new PlayTask(c, Priority.ORDER, arr, listener);
        mCurTask.play(mTaskCompleteListener);
      }
      LOCK.unlock();
    } else if (id == mCurTask.hashCode()) {
            /* 该任务正在播放，变化数据直接更新到当前的播放任务中 */
      mCurTask.updateTask(data);
      LOCK.unlock();
    } else {
      LOCK.unlock();
            /* 该任务是否在等待播放的任务列表中，找到该任务并更新其数据 */
      if (!mOrder.isEmpty() && data != null && data.mPlayText != null) {
        for (PlayTask task : mOrder) {
          if (id == task.hashCode()) {
            task.updateTask(data);
          }
        }
      }
    }
  }

  /**
   * 停止播放任务，自动开始下一个等待中的任务，如果有的话
   *
   * @param id 播放任务的id，调用play或manualPlay时返回的id
   */
  public synchronized void stop(int id) {
    LOCK.lock();
    if (mCurTask != null && id == mCurTask.hashCode()) {
            /* 在当前任务彻底停止之后，会调用TaskCompleteListener回调，回调中会开启下一个播放任务 */
      mCurTask.stop();
      LOCK.unlock();
    } else {
      LOCK.unlock();
            /*
             * 要停止的是等待中的任务
             * 将该任务从等待列表中移除
             */
      Iterator<PlayTask> it = null;
      if (!mOrder.isEmpty()) {
        it = mOrder.iterator();
        while (it.hasNext()) {
          PlayTask task = it.next();
          if (id == task.hashCode()) {
            try {
              it.remove();
            } catch (Exception e) {
            }
            return;
          }
        }
      }

      if (!mMsg.isEmpty()) {
        it = mMsg.iterator();
        while (it.hasNext()) {
          PlayTask task = it.next();
          if (id == task.hashCode()) {
            it.remove();
            return;
          }
        }
      }
    }
  }

  /**
   * 停止所有播放任务，并清除等待中的播放任务
   */
  public synchronized void stopAll() {
    mOrder.clear();
    mMsg.clear();
    LOCK.lock();
    if (mCurTask != null) {
      mCurTask.stop();
    }
    LOCK.unlock();
  }

  /**
   * 开始播放下一个等待中的任务
   */
  private void startNextTask() {
    LOCK.lock();
    if (mCurTask == null) {
      if (!mOrder.isEmpty()) {
        mCurTask = mOrder.remove(0);
      } else if (!mMsg.isEmpty()) {
        mCurTask = mMsg.remove(getPlayTaskIndex());
      }

      if (mCurTask != null) {
        mCurTask.play(mTaskCompleteListener);
      }
    }
    LOCK.unlock();
  }

  /**
   * 获取要播放的下一个任务的索引
   */
  private int getPlayTaskIndex() {
    int index = 0;
    int taskType = mMsg.get(0).getPriority().getValue();
    for (int i = 1, size = mMsg.size(); i < size; i++) {
      PlayTask task = mMsg.get(i);
      if (task == null) {
        continue;
      }
      int type = task.getPriority().getValue();
      if (taskType < type) {
        taskType = type;
        index = i;
      }
    }
    return index;
  }

  /**
   * 一个播放任务完成后的监听
   */
  private TaskCompleteListener mTaskCompleteListener = new TaskCompleteListener() {

    @Override
    public void onPlayTaskComplete(int code) {
      LOCK.lock();
      if (mCurTask != null && mCurTask.hashCode() == code) {
        mCurTask = null;
      }
      LOCK.unlock();
      startNextTask();
    }

  };

  /**
   * 播放任务结束的通知
   *
   * @note 用于和PlayTask的通信
   */
  interface TaskCompleteListener {

    /**
     * 播放任务完成
     *
     * @param code 完成的播放任务的hashCode()
     */
    void onPlayTaskComplete(int code);
  }
}
