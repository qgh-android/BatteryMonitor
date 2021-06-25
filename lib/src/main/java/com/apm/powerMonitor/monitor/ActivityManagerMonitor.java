package com.apm.powerMonitor.monitor;

import android.app.Service;
import android.content.Context;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Log;

import com.apm.powerMonitor.IMonitor;
import com.apm.powerMonitor.PowerMonitorManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ActivityManagerMonitor implements IMonitor {
    @Override
    public void register(Context context) {
        hookIActivityManager();
    }
    private class IActivityManagerHandler implements InvocationHandler {
        Object rawIActivityManager;


        public IActivityManagerHandler(Object rawIActivityManager) {
            this.rawIActivityManager = rawIActivityManager;
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if ("registerReceiver".equals(method.getName())) {
                IntentFilter intentFilter = (IntentFilter) args[3];
                StringBuilder stringBuilder = new StringBuilder("action =");
                if (intentFilter != null) {
                    int actionSize = intentFilter.countActions();
                    for (int i = 0; i < actionSize; i++) {
                        stringBuilder.append(", " + intentFilter.getAction(i));
                    }
                }
                Log.i(PowerMonitorManager.TAG, "ActivityManager, registerReceiver, action=" + stringBuilder.toString() +
                        ", args=" + Arrays.toString(args));
            } else if ("unregisterReceiver".equals(method.getName())) {

                Log.i(PowerMonitorManager.TAG, "ActivityManager, unregisterReceiver" + ", args=" + Arrays.toString(args));
            } else if ("serviceDoneExecuting".equals(method.getName())) {
                int res = (int) args[3];
                if (res != 0) {
                    IBinder iBinder = (IBinder) args[0];
                    Service service = getServices(iBinder);
                    Log.i(PowerMonitorManager.TAG,
                            "ActivityManager, serviceDoneExecuting, Service onStartCommand, serviceName=" + service.getClass().getName()
                                    + ", args=" + Arrays.toString(args));
                }

            } else if ("publishService".equals(method.getName())) {
                IBinder iBinder = (IBinder) args[0];
                Service service = getServices(iBinder);
                Log.i(PowerMonitorManager.TAG,
                        "ActivityManager, publishService, JobService, onStartJob, serviceName=" + service.getClass().getName()
                                + ", args=" + Arrays.toString(args));
            } else {
                Log.i(PowerMonitorManager.TAG, "method=" + method.getName() + ", args=" + Arrays.toString(args));
            }

            if (args != null) {
                return method.invoke(rawIActivityManager, args);
            } else {
                return method.invoke(rawIActivityManager);
            }
        }

    }

    public void hookIActivityManager() {

        try {
            //获取ActivityManager类对象
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            Field singletonDefaultField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            singletonDefaultField.setAccessible(true);
            Object singletonDefault = singletonDefaultField.get(null);

            //获取Singleton的原始mInstance对象
            Class<?> singleton = Class.forName("android.util.Singleton");
            Field mInstanceField = singleton.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object rawIActivityManager = mInstanceField.get(singletonDefault);
            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");

            //创建动态代理对象，并赋值给mInstance
            Object proxy = Proxy.newProxyInstance(activityManagerClass.getClassLoader(),
                    new Class<?>[]{iActivityManagerInterface}, new IActivityManagerHandler(rawIActivityManager));
            mInstanceField.set(singletonDefault, proxy);

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("xxx", " hook hookIActivityManager  err, e=" + e.toString());
        }
    }



    public Service getServices(IBinder iBinder) {
        Service services = null;
        try {
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Method sCurrentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
            sCurrentActivityThread.setAccessible(true);
            Object activityThreadObject = sCurrentActivityThread.invoke(activityThread);
            Field mServicesField = activityThread.getDeclaredField("mServices");
            mServicesField.setAccessible(true);
            ArrayMap<IBinder, Service> mServices = (ArrayMap<IBinder, Service>) mServicesField.get(activityThreadObject);
            services = mServices.get(iBinder);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return services;
    }
}
