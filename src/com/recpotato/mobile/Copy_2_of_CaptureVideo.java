package com.recpotato.mobile;

import java.io.IOException;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class Copy_2_of_CaptureVideo extends Activity implements OnClickListener, SurfaceHolder.Callback {
    MediaRecorder recorder;
    SurfaceHolder holder;
    boolean recording = false;
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    recorder = new MediaRecorder();
    initRecorder();
    setContentView(R.layout.capturevideo);

    SurfaceView cameraView = (SurfaceView) findViewById(R.id.surface_camera);


    holder = cameraView.getHolder();
    holder.addCallback(this);
    holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    cameraView.setClickable(true);
    cameraView.setOnClickListener(this);
}

private void initRecorder() {
    recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
    recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

    //recorder.setProfile(cpHigh);
    recorder.setOutputFile("/sdcard/videocapture_example.mp4");
    recorder.setMaxDuration(30000); // 50 seconds
    recorder.setMaxFileSize(4000000); // Approximately 5 megabytes
}

private void prepareRecorder() {
    recorder.setPreviewDisplay(holder.getSurface());

    try {
        recorder.prepare();
    } catch (IllegalStateException e) {
        e.printStackTrace();
        finish();
    } catch (IOException e) {
        e.printStackTrace();
        finish();
    }
}

public void onClick(View v) {
    if (recording) {
        recorder.stop();
        recording = false;

        // Let's initRecorder so we can record again
        initRecorder();
        prepareRecorder();
    } else {
        recording = true;
        recorder.start();
    }
}

public void surfaceCreated(SurfaceHolder holder) {
    prepareRecorder();
}

public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
}

public void surfaceDestroyed(SurfaceHolder holder) {
    if (recording) {
        recorder.stop();
        recording = false;
    }
    recorder.release();
    finish();
}
}

