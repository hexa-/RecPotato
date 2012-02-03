package com.recpotato.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.widget.Toast;

public class CopyOfCaptureVideo extends Activity {
	

    protected boolean _taken = true;
    File sdImageMainDirectory;

    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);         
            File root = new File(Environment.getExternalStorageDirectory()
            		+ File.separator + "myDir" + File.separator);
            root.mkdirs();
            sdImageMainDirectory = new File(root, "myPicName");

            startCameraActivity();

        } catch (Exception e) {
            finish();
            Toast.makeText(this, "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }

    }

    protected void startCameraActivity() {

        Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            try {
                StoreImage(this, Uri.parse(data.toURI()), sdImageMainDirectory);
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
            startActivity(new Intent(CopyOfCaptureVideo.this, RecPotato.class));
        }
        else {
        	Toast.makeText(this, "Not stored", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean(CopyOfCaptureVideo.PHOTO_TAKEN)) {
            _taken = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CopyOfCaptureVideo.PHOTO_TAKEN, _taken);
    }

    public static void StoreImage(Context mContext, Uri imageLoc, File imageDir) {
	    Bitmap bm = null;
	    try {
	        bm = Media.getBitmap(mContext.getContentResolver(), imageLoc);
	        FileOutputStream out = new FileOutputStream(imageDir);
	        bm.compress(Bitmap.CompressFormat.JPEG, 100, out);
	        bm.recycle();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

    }


}
