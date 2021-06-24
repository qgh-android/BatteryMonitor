package com.apm.powermonitor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    private String TAG = "MainActivity";
    WifiManager wifiManager;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_playVoice).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                startVoice();
            }
        });
        findViewById(R.id.btn_stopVoice).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                stopVoice();
            }
        });
        findViewById(R.id.btn_JobMonitor).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Log.i(TAG, "schedule");
                JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
                JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(MainActivity.this, TestJobService.class));  //指定哪个JobService执行操作
                builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(10)); //执行的最小延迟时间
                builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(15));  //执行的最长延时时间
                builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);  //非漫游网络状态
                builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
                builder.setRequiresCharging(false); // 未充电状态
                jobScheduler.schedule(builder.build());

            }
        });
        findViewById(R.id.btn_AlarmMonitor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = 1 * 1000 * 30;//1分钟
                AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(MainActivity.this, AlarmRepetitionService.class);
                PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT < 19) {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
                }
            }
        });
        findViewById(R.id.btn_wifiMonitor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLoactionPermission()) {
                    return;
                }
                wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(false);
                wifiManager.startScan();
                WifiReceiver receiver = new WifiReceiver();
                IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(receiver, filter);

            }
        });
        findViewById(R.id.btn_wakeLockMonitor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                @SuppressLint("InvalidWakeLockTag") final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "TAG");

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "acquire");
                        wakeLock.acquire();//亮屏
                    }
                }, 5 * 1000);


                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "release");
                        wakeLock.release();//熄灭
                    }
                }, 10 * 1000);

            }
        });
        findViewById(R.id.btn_locationMonitor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkLoactionPermission()) {
                    return;
                }


                // 获取位置并显示
                LocationManager locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
//                locationManager.requestLocationUpdates();
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    String message = "(" + location.getLongitude() + ", " + location.getLatitude() + ")";
                    Log.i(TAG, message);
                }

                String locationProvider = LocationManager.GPS_PROVIDER;
                locationManager.requestLocationUpdates(locationProvider, 1000, 0, new LocationListener() {
                    // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle arg2) {
                        Log.i(TAG, "onStatusChanged");
                    }

                    // Provider被enable时触发此函数，比如GPS被打开
                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.i(TAG, "onProviderEnabled");
                    }

                    // Provider被disable时触发此函数，比如GPS被关闭
                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.i(TAG, "onProviderDisabled");
                    }

                    //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                    @Override
                    public void onLocationChanged(Location loc) {
                        Log.i(TAG, "onLocationChanged");
                    }
                });

            }
        });
    }

    public void startVoice() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.shuidi);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mediaPlayer == null) {
                    return;
                }
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
    }

    public void stopVoice() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); //切记一定要release
            mediaPlayer = null;
        }
    }

    private boolean checkLoactionPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "没有定位权限", Toast.LENGTH_SHORT).show();
            String[] Permissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(MainActivity.this,
                    Permissions,
                    1);
            return false;
        }
        return true;
    }

    public class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //得到wifi扫描的结果
            System.out.println("===得到扫描结果:");
            List<ScanResult> scanResults = wifiManager.getScanResults();
            List<String> infos = new ArrayList();
            for (ScanResult scanResult : scanResults) {
                infos.add(scanResult.SSID);

            }
            System.out.println("===infos:" + Arrays.toString(infos.toArray()));
        }

    }
}