package com.recpotato.mobile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class RecPotato extends ListActivity {

	private static final String TAG = "Recpotato Main";

	protected static final int TASK_DONE = 5;
	private DBAdapter db = new DBAdapter(this);
	private Cursor tasksCursor = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		getNewJson();
		
		parseJson();
		refreshList();
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		lv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				Intent taskIntent = new Intent(view.getContext(), TaskView.class);
				taskIntent.putExtra("id", tasksCursor.getLong(tasksCursor.getColumnIndex(DBAdapter.KEY_ROWID)));
				startActivityForResult(taskIntent, TASK_DONE);
			}
		});

	}
	
	public void onResume() {  // After a pause OR at startup
	    super.onResume();
	    refreshList();
     }
	
	private void refreshList() {
		db.open();
		tasksCursor = db.getAllTasks();
		startManagingCursor(tasksCursor);
		Log.w(TAG, "Database has " + tasksCursor.getCount() + " items");
		String[] columns = new String[] {DBAdapter.KEY_NAME};
		int[] to = new int[] {R.id.name_entry};
		SimpleCursorAdapter itemsAdapter = new SimpleCursorAdapter(this, R.layout.tasklist_item, tasksCursor, columns, to);
		db.close();
		setListAdapter(itemsAdapter);
		
	}


	private void getNewJson() {

		/*
		ProgressDialog mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("Downloading tasks..");
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		*/
		DownloadJson downloadFile = new DownloadJson();
		String pathTo = new File(getCacheDir(), "tasks.json").getAbsolutePath();
		downloadFile.execute("http://46.51.175.244:1337/tasks.json", pathTo);
		
	}
	
	private void parseJson() {
		db.open();
		
		try {
			File jsonFile = new File(getCacheDir(), "tasks.json");
			InputStream inStream = new BufferedInputStream(new FileInputStream(jsonFile));

			StringBuilder builder = new StringBuilder();
			String json = null;
			//db.open();
			db.clearTasks();
			int r;
	        do {
	            r = inStream.read();
	            if (r != -1)
	                builder.append((char) r);
	        } while (r != -1);
			
			json = builder.toString();
			
			JSONArray ja = new JSONArray(json);
				
			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = (JSONObject) ja.get(i);
				/*
				Log.e(TAG, "Json Parse : " + jo.getString("Name"));
				Log.e(TAG, "Json Parse : " + jo.getString("Location"));
				Log.e(TAG, "Json Parse : " + jo.getString("Value"));
				Log.e(TAG, "Json Parse : " + jo.getString("Intro description"));
				 */
				
				db.insertTask(jo.getString("Name"),
						jo.getString("Location"),
						jo.getString("Value"),
						jo.getString("Intro description"));
				
			}
			

			db.close();
		}
	    catch (Exception e) {
	        // TODO Auto-generated catch block
	    	Log.e(TAG, "Json Parse FAILED: " + e);
	        e.printStackTrace();
	    } 
	}
	
	/*
    public void onProgressUpdate(int... args){
        // here you will have to update the progressbar
        // with something like
        mProgressDialog.setProgress(args[0]);
    }
    
    */


}

