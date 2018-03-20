package com.cretin.studyhelper.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;

import com.cetin.studyhelper.R;

public class NotifyHelper {

    private static Vibrator sVibrator;
    private static KeyguardManager sKeyguardManager;
    private static PowerManager sPowerManager;

    /**
     * 播放声音
     */
    public static void sound(Context context) {
        try {
            MediaPlayer player = MediaPlayer.create(context,
                    Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.piano));
//                    Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            player.start();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 振动
     */
    public static void vibrator(Context context) {
        if ( sVibrator == null ) {
            sVibrator = ( Vibrator ) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        sVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
    }
}
