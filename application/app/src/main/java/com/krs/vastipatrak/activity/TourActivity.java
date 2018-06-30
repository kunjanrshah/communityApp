package com.krs.vastipatrak.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.krs.vastipatrak.R;

public class TourActivity extends Activity {

    private VideoView video_player_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tour);
        video_player_view = findViewById(R.id.video_player_view);
       /* Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.connectingcommunty1);
        video_player_view.setVideoURI(video);
*/
        video_player_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                video_player_view.start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        video_player_view.start();
    }
}
