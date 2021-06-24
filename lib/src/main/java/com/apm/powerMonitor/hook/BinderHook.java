package com.apm.powerMonitor.hook;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public class BinderHook {
    private String serviceName;
    private String IBinderName;
    private HookCallBack hookCallBack;

    public void hook(String SERVICE_NAME, String IBinderName, HookCallBack hookCallBack) {
        this.serviceName = SERVICE_NAME;
        this.IBinderName = IBinderName;
        this.hookCallBack = hookCallBack;

        try {
            // 1. 获取系统自己的Binder
            Class<?> serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getDeclaredMethod("getService", String.class);
            // 2. 创建我们自己的Binder，动态代理了 queryLocalInterface 方法。
            IBinder rawBinder = (IBinder) getService.invoke(null, serviceName);
            IBinder hookedBinder = (IBinder) Proxy.newProxyInstance(serviceManager.getClassLoader(),
                    new Class<?>[]{IBinder.class},
                    new BinderProxyHookHandler(rawBinder));
            // 3. 获取 ServiceManager 中的 sCache
            Field cacheField = serviceManager.getDeclaredField("sCache");
            cacheField.setAccessible(true);
            Map<String, IBinder> cache = (Map) cacheField.get(null);
            // 4. 将自定义的 Binder 对象替换掉旧的系统 Binder
            cache.put(serviceName, hookedBinder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class BinderProxyHookHandler implements InvocationHandler {

        private static final String TAG = "BinderProxyHookHandler";

        // 绝大部分情况下,这是一个BinderProxy对象
        // 只有当Service和我们在同一个进程的时候才是Binder本地对象
        // 这个基本不可能
        IBinder base;

        Class<?> stub;

        Class<?> iinterface;

        public BinderProxyHookHandler(IBinder base) {
            this.base = base;
            try {
                this.stub = Class.forName(IBinderName + "$Stub");
                this.iinterface = Class.forName(IBinderName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("queryLocalInterface".equals(method.getName())) {
                //Log.d(TAG, "hook queryLocalInterface");
                // 这里直接返回真正被Hook掉的Service接口
                // 这里的 queryLocalInterface 就不是原本的意思了
                // 我们肯定不会真的返回一个本地接口, 因为我们接管了 asInterface方法的作用
                // 因此必须是一个完整的 asInterface 过的 IInterface对象, 既要处理本地对象,也要处理代理对象
                // 这只是一个Hook点而已, 它原始的含义已经被我们重定义了; 因为我们会永远确保这个方法不返回null
                // 让 IClipboard.Stub.asInterface 永远走到if语句的else分支里面
                return Proxy.newProxyInstance(proxy.getClass().getClassLoader(),
                        // asInterface 的时候会检测是否是特定类型的接口然后进行强制转换
                        // 因此这里的动态代理生成的类型信息的类型必须是正确的
                        new Class[]{IBinder.class, IInterface.class, this.iinterface},
                        new BinderHookHandler(base, stub));
            }

            Log.d(TAG, "method:" + method.getName());
            return method.invoke(base, args);
        }
    }

    public class BinderHookHandler implements InvocationHandler {

        private static final String TAG = "BinderHookHandler";

        // 原始的Service对象 (IInterface)
        Object base;

        public BinderHookHandler(IBinder base, Class<?> stubClass) {
            try {
                Method asInterfaceMethod = stubClass.getDeclaredMethod("asInterface", IBinder.class);
                // IClipboard.Stub.asInterface(base);
                this.base = asInterfaceMethod.invoke(null, base);
            } catch (Exception e) {
                throw new RuntimeException("hooked failed!");
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {


            return hookCallBack.invoke(base, method, args);
//            return method.invoke(base, args);
        }
    }

    public interface HookCallBack {
        Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
    }
}
