package com.apm.powerMonitor.monitor;

import android.content.Context;
import android.util.Log;

import com.apm.powerMonitor.IMonitor;
import com.apm.powerMonitor.PowerMonitorManager;
import com.apm.powerMonitor.hook.BinderHook;

import java.lang.reflect.Method;
import java.util.Arrays;

public class LocationMonitor implements IMonitor {
    @Override
    public void register(Context context) {
        BinderHook binderHook = new BinderHook();
        binderHook.hook(Context.LOCATION_SERVICE, "android.location.ILocationManager", new BinderHook.HookCallBack() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("requestLocationUpdates".equals(method.getName())) {
                    Log.i(PowerMonitorManager.TAG, "ILocationManager, requestLocationUpdates, args=" + Arrays.toString(args));
                }
                return method.invoke(proxy, args);
            }
        });
    }
}
