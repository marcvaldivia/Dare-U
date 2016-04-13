package com.dare_u.dare_u;

import android.content.Intent;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.dare_u.constants.Preferences;
import com.dare_u.objects.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoTakerActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;

    ImageButton btnTake;
    ImageView btnSwitch;

    File mCurrentPhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_taker);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Get path
        mCurrentPhoto = getOutputMediaFile();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Add a listener to the Capture button
        btnTake = (ImageButton) findViewById(R.id.btnTake);
        btnTake.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TakePictureTask().execute();
                    }
                }
        );

        // ImageView
        btnSwitch = (ImageView) findViewById(R.id.btnSwitch);
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * Set visible the button tu accept the photo.
     */
    private void photoTaken() {
        Intent data = new Intent();
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
        finish();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
            c.setDisplayOrientation(90); // Portrait orientation
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(){
        String path = getIntent().getStringExtra(Preferences.FILE);
        return new File(path);
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            if (mCurrentPhoto == null){
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(mCurrentPhoto);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
    };

    class TakePictureTask extends AsyncTask<Void, Void, Void> {

        protected void onPreExecute () {
            btnTake.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void result) {
            releaseCamera();
            photoTaken();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mCamera.takePicture(null, null, mPicture);

            // Sleep for however long, you could store this in a variable and
            // have it updated by a menu item which the user selects.
            try {
                Thread.sleep(2000); // 2 second preview
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

    }

}
