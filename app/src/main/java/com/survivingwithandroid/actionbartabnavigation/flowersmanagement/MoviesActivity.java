package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.survivingwithandroid.actionbartabnavigation.R;

/**
 * Created by Jan Lipka on 2015-07-29.
 */
public class MoviesActivity extends Activity {

    private static final String MOVIE_URL = "http://www.archive.org/download/Unexpect2001/Unexpect2001_512kb.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        VideoView videoView = (VideoView) findViewById(R.id.video);
        MediaController mediaController = new MediaController(this);
        Uri videoUri = Uri.parse(MOVIE_URL);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
    }
}
