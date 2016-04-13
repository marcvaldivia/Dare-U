package com.dare_u.dare_u;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dare_u.constants.Preferences;
import com.dare_u.objects.CameraPreview;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VideoRecorderActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    MediaRecorder mMediaRecorder;
    CountDownTimer count;

    TextView txtTime;
    ImageButton btnRecord;
    ImageView btnSwitch;

    private boolean isRecording = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_recorder);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // TextView
        txtTime = (TextView) findViewById(R.id.txtTime);

        // Add a listener to the Capture button
        btnRecord = (ImageButton) findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isRecording) {
                            stop();
                        } else {
                            record();
                        }
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
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private boolean prepareVideoRecorder(){

        //mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        mMediaRecorder.setMaxDuration(7000);

        // Step 4: Set output file
        //mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setOutputFile(getOutputMediaFile().toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        String path = getIntent().getStringExtra(Preferences.FILE);
        return new File(path);
    }

    /**
     * Record a video.
     */
    private void record() {
        // initialize video camera
        if (prepareVideoRecorder()) {
            // Camera is available and unlocked, MediaRecorder is prepared,
            // now you can start recording

            count = new CountDownTimer(7000, 1000) {

                public void onTick(long millisUntilFinished) {
                    txtTime.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    stop();
                }
            }.start();

            mMediaRecorder.start();

            // inform the user that recording has started
            btnRecord.setImageResource(R.drawable.ic_stop_white_48dp);
            isRecording = true;
        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder();
        }
    }

    /**
     * Stops the recording of the video.
     */
    private void stop() {
        if (count != null) {
            count.cancel();
        }
        // stop recording and release camera
        mMediaRecorder.stop();  // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object
        mCamera.lock();         // take camera access back from MediaRecorder

        // inform the user that recording has stopped
        btnRecord.setImageResource(R.drawable.ic_videocam_white_48dp);
        isRecording = false;
        // Return a result OK code
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
