package com.apm.powerMonitor;

import android.content.Context;

import com.apm.powerMonitor.monitor.ActivityManagerMonitor;
import com.apm.powerMonitor.monitor.AlarmMonitor;
import com.apm.powerMonitor.monitor.LocationMonitor;
import com.apm.powerMonitor.monitor.JobMonitor;
import com.apm.powerMonitor.monitor.WakeLockMonitor;
import com.apm.powerMonitor.monitor.WifiMonitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PowerMonitorManager {
    public static final String TAG = "PowerMonitor";

    public static void init(Context context) {

        ActivityManagerMonitor activityManagerMonitor = new ActivityManagerMonitor();
        activityManagerMonitor.register(context);

        WakeLockMonitor wakeLockMonitor = new WakeLockMonitor();
        wakeLockMonitor.register(context);

        LocationMonitor locationMonitor = new LocationMonitor();
        locationMonitor.register(context);

        WifiMonitor wifiMonitor = new WifiMonitor();
        wifiMonitor.register(context);


        AlarmMonitor alarmMonitor = new AlarmMonitor();
        alarmMonitor.register(context);

        JobMonitor jobMonitor = new JobMonitor();
        jobMonitor.register(context);

//        dumpPhoneInfo();
    }

//    private static void dumpPhoneInfo() {
//        ScheduledExecutorService executorService= Executors.newScheduledThreadPool(1);
//        executorService.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },0,5, TimeUnit.SECONDS);
//    }
}
