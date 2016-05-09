package licenta.fastbanking.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import licenta.fastbanking.R;

/**
 * Created by Tiberiu Visan on 5/9/2016.
 * Project: FastBanking
 */
public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        VideoView videoView = (VideoView) findViewById(R.id.video_view);
        String videoViewUri = getIntent().getStringExtra("videoURI");

        Log.d("VIDEO ACT", videoViewUri);
        MediaController mc=new MediaController(this);
        mc.setEnabled(true);
        mc.show(0);
        assert videoView != null;
        videoView.setMediaController(mc);

        videoView.setVideoURI(Uri.parse(videoViewUri));
        videoView.requestFocus();
        videoView.showContextMenu();
        videoView.start();


    }
}
