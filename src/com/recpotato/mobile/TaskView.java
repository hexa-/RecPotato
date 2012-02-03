package com.recpotato.mobile;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TaskView extends Activity {
	

	protected static final int VIDEO_TAKEN = 4;
	protected static final String TAG = "RP TaskWiew";
	private DBAdapter db = new DBAdapter(this);

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.taskview);
		
		long taskId = this.getIntent().getExtras().getLong("id");
		
		db.open();
		
		Cursor task = db.getTask(taskId);
		((TextView) findViewById(R.id.taskHeading)).setText(task.getString(task.getColumnIndex(DBAdapter.KEY_NAME)));
		((TextView) findViewById(R.id.taskDescription)).setText(task.getString(task.getColumnIndex(DBAdapter.KEY_DESCRIPTION)));
		((TextView) findViewById(R.id.locationValue)).setText(task.getString(task.getColumnIndex(DBAdapter.KEY_LOCATION)));
		((TextView) findViewById(R.id.priceValue)).setText(task.getString(task.getColumnIndex(DBAdapter.KEY_VALUE)));
		
		db.close();
		
		Button b = (Button) findViewById(R.id.taskShootButton);
		b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String cacheFileName = "video.3gp";
				
				File tempFile = new File(getCacheDir(),cacheFileName);
				Log.e(TAG,tempFile.getPath());
			
				Intent videoIntent = new Intent("android.media.action.VIDEO_CAPTURE");
				videoIntent.putExtra("EXTRA_VIDEO_QUALITY", 5);
				videoIntent.putExtra("output", tempFile.getPath());
				videoIntent.putExtra("android.intent.extra.durationLimit", 30);
				videoIntent.putExtra("android.intent.extra.finishOnCompletion", true);
				Log.e(TAG, videoIntent.getExtras().toString());
				
				//Intent videoIntent = new Intent(v.getContext(), CaptureVideo.class);
				startActivityForResult(videoIntent, VIDEO_TAKEN);
			}
		});
		
	}
	
	 @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  super.onActivityResult(requestCode, resultCode, data);
		  if(resultCode == RESULT_OK && requestCode == VIDEO_TAKEN){
			  Log.e(TAG,"CaptureActivity");
			  
			  Uri selectVideo = data.getData();
			  String [] pathColumn = {MediaStore.Video.Media.DATA};
			  
			  Cursor c = getContentResolver().query(selectVideo, pathColumn, null, null, null);
			  c.moveToFirst();
			  
			  int cI = c.getColumnIndex(pathColumn[0]);
			  String path = c.getString(cI);
			  c.close();

			  Intent uploader = new Intent(this, HttpUploader.class);
			  uploader.putExtra("path", path);
			  startService(uploader);
			  
			  setResult(RESULT_OK, null);
			  finish();
		  } else {
			  setResult(RESULT_CANCELED, null);
			  finish();
		  }
	}
	
}
