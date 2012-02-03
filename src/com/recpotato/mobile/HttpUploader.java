package com.recpotato.mobile;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class HttpUploader extends IntentService {
	public HttpUploader() {
		super("HttpUploader");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Uploader Service Started", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "Uploader Service Stopped", Toast.LENGTH_SHORT).show();
		super.onDestroy();
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		

		String pathToOurFile = intent.getExtras().getString("path");

		//Toast.makeText(this, "Path to file: " + pathToOurFile, Toast.LENGTH_SHORT).show();
		
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		
		String urlServer = "http://46.51.175.244:1337/";
		
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;

		try {
			File f = new File(pathToOurFile);
			FileInputStream fileInputStream = new FileInputStream(f);
	
			URL url = new URL(urlServer);
			connection = (HttpURLConnection) url.openConnection();

			// Allow Inputs & Outputs
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
	
			// Enable POST method
			connection.setRequestMethod("POST");
	
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

			outputStream = new DataOutputStream( connection.getOutputStream() );
			
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);  //" + pathToOurFile +"
			outputStream.writeBytes("Content-Disposition: form-data; name=\"upload[file]\"; filename=\"Android\"" + lineEnd);
			outputStream.writeBytes("Content-Type: application/octet-stream\r\nContent-Transfer-Encoding: binary" + lineEnd);

			outputStream.writeBytes(lineEnd);
			
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
				
			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
			connection.getResponseCode();
			connection.getResponseMessage();
	
			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			Toast.makeText(getApplicationContext(), "Upload done, thank you!", Toast.LENGTH_LONG).show();
			//tempFile.delete();
			stopSelf();
		}
		catch (Exception ex) {
			Toast.makeText(getApplicationContext(), "FAIL with upload, please retry!", Toast.LENGTH_LONG).show();
			Log.e("RP_HTTPUploader",ex.getMessage());
			stopSelf();
		//Exception handling
		}
		
	}
}