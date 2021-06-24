package com.apm.powermonitor;

import android.app.Application;
import android.media.MediaPlayer;
import android.util.Log;

import com.apm.powerMonitor.PowerMonitorManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PowerMonitorManager.init(this);

    }
    MediaPlayer mediaPlayer  = null;
    public void test() {
        MediaPlayer mediaPlayer = null;
        mediaPlayer.start();
    }

//    LDC "PowerMonitor"
//    LDC "mediaPlayer.start"
//    INVOKESTATIC android/util/Log.i (Ljava/lang/String;Ljava/lang/String;)I
//            POP
    public void startVoice() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.shuidi);
        mediaPlayer.start();
        Log.i("PowerMonitor","mediaPlayer.start");
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
}
