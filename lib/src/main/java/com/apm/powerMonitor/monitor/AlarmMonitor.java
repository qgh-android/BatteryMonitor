package com.apm.powerMonitor.monitor;

import android.content.Context;
import android.util.Log;

import com.apm.powerMonitor.IMonitor;
import com.apm.powerMonitor.PowerMonitorManager;
import com.apm.powerMonitor.hook.BinderHook;

import java.lang.reflect.Method;
import java.util.Arrays;

public class AlarmMonitor implements IMonitor {
    @Override
    public void register(Context context) {
        BinderHook binderHook = new BinderHook();

        binderHook.hook(Context.ALARM_SERVICE, "android.app.IAlarmManager", new BinderHook.HookCallBack() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("set".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "AlarmMonitor.set, args=" + Arrays.toString(args));
                }
                if ("setRepeating".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "AlarmMonitor.setRepeating, args=" + Arrays.toString(args));
                }
                if ("setInexactRepeating".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "AlarmMonitor.set, args=" + Arrays.toString(args));
                }
                if ("cancel".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "AlarmMonitor.set, args=" + Arrays.toString(args));
                }
                if ("setExact".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "AlarmMonitor.setExact, args=" + Arrays.toString(args));
                }
                return method.invoke(proxy, args);
            }
        });
    }
}
