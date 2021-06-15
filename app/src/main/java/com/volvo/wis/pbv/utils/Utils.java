package com.volvo.wis.pbv.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.volvo.wis.pbv.R;
import com.volvo.wis.pbv.helpers.Log4jHelper;

import java.io.IOException;

public class Utils {

    public static void beep(Context activity) {
        MediaPlayer player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        try {
            AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.beep);
            player.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            file.close();
            player.setVolume(.1f, .1f);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log4jHelper.getLogger(Utils.class.getName()).error("Erro ao executar beep.", e);
        } finally {
            while (!player.isPlaying()){
                player.release();
                break;
            }
        }
    }
}
