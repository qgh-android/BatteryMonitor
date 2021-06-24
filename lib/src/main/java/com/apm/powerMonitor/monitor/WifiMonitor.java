package com.apm.powerMonitor.monitor;

import android.content.Context;
import android.util.Log;

import com.apm.powerMonitor.PowerMonitorManager;
import com.apm.powerMonitor.hook.BinderHook;
import com.apm.powerMonitor.IMonitor;

import java.lang.reflect.Method;

public class WifiMonitor implements IMonitor {
    @Override
    public void register(Context context) {

        BinderHook binderHook = new BinderHook();
        binderHook.hook(Context.WIFI_SERVICE, "android.net.wifi.IWifiManager", new BinderHook.HookCallBack() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("setWifiEnabled")) {
                    Log.i(PowerMonitorManager.TAG, "IWifiManager->setWifiEnabled");
                } else if (method.getName().equals("startScan")) {
                    Log.i(PowerMonitorManager.TAG, "IWifiManager->startScan");
                }
                return method.invoke(proxy, args);
            }
        });

    }
}
