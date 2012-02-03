package com.recpotato.mobile;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import android.os.AsyncTask;
import android.util.Log;


public class DownloadJson extends AsyncTask<String, Integer, String>{
	
	private static final String TAG = "RP DownloadJson";
   
	protected String doInBackground(String... uri) {
        int count;
        
        try {
        	Log.d(TAG, "Started Json Download! from " + uri[0]);
            URL url = new URL(uri[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            // this will be useful so that you can show a tipical 0-100% progress bar
            int lenghtOfFile = conexion.getContentLength();

            // downlod the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(uri[1]);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                publishProgress((int)(total*100/lenghtOfFile));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            Log.d(TAG, "Json Download Complete!");
            
            
        } catch (Exception e) {
        	Log.e(TAG, "Json Download FAILED: " + e);
        }
        return null;
    }
    
   
}
