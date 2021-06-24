package com.apm.powerMonitor.monitor;

import android.content.Context;
import android.util.Log;

import com.apm.powerMonitor.IMonitor;
import com.apm.powerMonitor.PowerMonitorManager;
import com.apm.powerMonitor.hook.BinderHook;

import java.lang.reflect.Method;
import java.util.Arrays;

public class JobMonitor implements IMonitor {
    @Override
    public void register(Context context) {

        BinderHook binderHook = new BinderHook();
        binderHook.hook(Context.JOB_SCHEDULER_SERVICE, "android.app.job.IJobScheduler", new BinderHook.HookCallBack() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("enqueue".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "IJobScheduler, enqueue, args=" + Arrays.toString(args));
                }
                if ("schedule".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "IJobScheduler, schedule, args=" + Arrays.toString(args));
                }
                if ("cancel".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "IJobScheduler, cancel, args=" + Arrays.toString(args));
                }
                if ("cancelAll".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "IJobScheduler, cancelAll, args=" + Arrays.toString(args));
                }
                return method.invoke(proxy, args);
            }
        });
    }
}
