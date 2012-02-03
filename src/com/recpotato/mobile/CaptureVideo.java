package com.recpotato.mobile;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

public class CaptureVideo extends Activity implements OnClickListener, SurfaceHolder.Callback {
	private static final String TAG = "CAMERA_TUTORIAL";

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private boolean previewRunning;
	
    private MediaRecorder mediaRecorder;
	private final int maxDurationInMs = 20000;
	private final long maxFileSizeInBytes = 500000;
	private final int videoFramesPerSecond = 15;
	
	boolean recording = false;



    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.capturevideo);
            surfaceView = (SurfaceView) findViewById(R.id.surface_camera);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            
            surfaceView.setClickable(true);
            surfaceView.setOnClickListener(this);
            
            Toast.makeText(getApplicationContext(), "Touch to record", Toast.LENGTH_LONG).show();
    }

    @Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(352, 288);
		parameters.set("orientation", "portrait");
		camera.setParameters(parameters);
		
		if (camera != null){
			Camera.Parameters params = camera.getParameters();
			camera.setParameters(params);
		}
		else {
			Toast.makeText(getApplicationContext(), "Camera not available!", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		if (previewRunning){
			camera.stopPreview();
		}
		Camera.Parameters p = camera.getParameters();
		//p.setPreviewSize(width, height);
		//p.setPreviewFormat(PixelFormat.JPEG);
		camera.setParameters(p);

		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			previewRunning = true;
		}
		catch (IOException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		previewRunning = false;
		camera.release();
	}
	
	public void stopRecording(){
        try{
            mediaRecorder.stop();
            mediaRecorder.release();
        }
        catch (IllegalStateException e) {
        	setResult(RESULT_CANCELED, null);

        	Toast.makeText(getApplicationContext(), "FUUUUUUUUUUUUUUU!", Toast.LENGTH_LONG).show();
        }
        //camera.lock();

	}
	
	public boolean startRecording(){
		try {
			camera.stopPreview();
			camera.unlock();

			mediaRecorder = new MediaRecorder();

			mediaRecorder.setCamera(camera);
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			
			String cacheFileName = "video.3gp";
			File tempFile = new File(getCacheDir(),cacheFileName);
			mediaRecorder.setOutputFile(tempFile.getPath());
			
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
			
			mediaRecorder.setVideoFrameRate(videoFramesPerSecond);
			mediaRecorder.setMaxDuration(maxDurationInMs);

			
			/*
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File (sdCard.getAbsolutePath());
            File tempFile = new File(dir, "myvideo.3gp");
            mediaRecorder.setOutputFile(tempFile.getPath());
            */
			
			
			//mediaRecorder.setVideoSize(surfaceView.getWidth(), surfaceView.getHeight());
			mediaRecorder.setVideoSize(480, 320);
			//mediaRecorder.setVideoSize(320, 240);
			


			mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());

			mediaRecorder.setMaxFileSize(maxFileSizeInBytes);

            mediaRecorder.prepare();
			mediaRecorder.start();

			return true;
		} catch (IllegalStateException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			Log.e(TAG,e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		
	    if (recording) {
	    	stopRecording();
	    	//Toast.makeText(getApplicationContext(), "Rec ended", Toast.LENGTH_LONG).show();
	        recording = false;
	        setResult(RESULT_OK, null);
	        finish();
	    } else {
	        recording = true;
	        //Toast.makeText(getApplicationContext(), "Rec started!", Toast.LENGTH_SHORT).show();
	        startRecording();
	    }

		
	}
}

