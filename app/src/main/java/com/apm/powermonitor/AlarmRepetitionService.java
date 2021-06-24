package com.apm.powermonitor;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;


/**
 * AlarmManager.ELAPSED_REALTIME
 * 在指定的延时过后，发送广播，但不唤醒设备（闹钟在睡眠状态下不可用）。如果在系统休眠时闹钟触发，它将不会被传递，直到下一次设备唤醒。设备休眠时并不会唤醒设备;
 * AlarmManager.ELAPSED_REALTIME_WAKEUP
 * 在指定的延时过后，发送广播，并唤醒设备（即使关机也会执行operation所对应的组件）。与ELAPSED_REALTIME基本功能一样，只是会在设备休眠时唤醒设备;
 * AlarmManager.RTC
 * 使用绝对时间，可以通过 System.currentTimeMillis()获取，设备休眠时并不会唤醒设备;
 * AlarmManager.RTC_WAKEUP
 * 与RTC基本功能一样，只是会在设备休眠时唤醒设备
 * <p>
 * AlarmManager.INTERVAL_FIFTEEN_MINUTES 间隔15分钟
 * AlarmManager.INTERVAL_HALF_HOUR 间隔半个小时
 * AlarmManager.INTERVAL_HOUR 间隔一个小时
 * AlarmManager.INTERVAL_HALF_DAY 间隔半天
 * AlarmManager.INTERVAL_DAY 间隔一天
 */
public class AlarmRepetitionService extends IntentService {


    public static final String TAG = "AlarmRepetitionService";


    public AlarmRepetitionService() {
        super("RepetitionService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        Log.d(TAG, "onStart");
        super.onStart(intent, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onStart");
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");
//        做一些逻辑，由于IntentService 会进行异步处理，
//        所以这里可以直接写耗时逻辑，不会占用主线程耗时，不需要再开启异步线程，
//        onHandleIntent 执行完后， Service会自动销毁；
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}

