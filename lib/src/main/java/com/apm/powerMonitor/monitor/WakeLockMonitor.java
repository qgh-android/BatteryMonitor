package com.apm.powerMonitor.monitor;

import android.content.Context;
import android.util.Log;

import com.apm.powerMonitor.PowerMonitorManager;
import com.apm.powerMonitor.hook.BinderHook;
import com.apm.powerMonitor.IMonitor;

import java.lang.reflect.Method;

public class WakeLockMonitor implements IMonitor {
    private static final String POWER_SERVICE = "power";

    @Override
    public void register(Context context) {
        Log.i("powerMonitor", "**********************WakeLockMonitor->register**********************");
        BinderHook binderHook = new BinderHook();
        binderHook.hook(POWER_SERVICE, "android.os.IPowerManager",  new BinderHook.HookCallBack() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("acquireWakeLock")) {
                    Log.i(PowerMonitorManager.TAG, "PowerManager->acquireWakeLock");
                } else if (method.getName().equals("releaseWakeLock")) {
                    Log.i(PowerMonitorManager.TAG, "PowerManager->releaseWakeLock");
                }
                return method.invoke(proxy, args);
            }
        });
    }

}
