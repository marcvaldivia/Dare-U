package com.dare_u.dare_u;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dare_u.behaviour.ConnectFileActivity;
import com.dare_u.behaviour.OptionsResponsive;
import com.dare_u.camera.PhotoEditor;
import com.dare_u.camera.VideoEditor;
import com.dare_u.constants.Errors;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.Challenge;
import com.dare_u.domain.Contact;
import com.dare_u.domain.User;
import com.dare_u.connect.DataFileConnect;
import com.dare_u.objects.AlertDialogOptions;
import com.dare_u.objects.VideoU;
import com.dare_u.utils.DataAccess;
import com.dare_u.utils.General;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener, ConnectFileActivity, OptionsResponsive {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_VIDEO_CAPTURE = 2;
    private String mCurrentPhotoPath = null;
    private String mCurrentVideoPath = null;

    User user;
    Challenge challenge;

    TextView txtChallenge;
    ImageButton btnPhoto, btnVideo, btnCancel;
    ImageView ivPhoto;
    VideoU vvVideo;

    LinearLayout progressUpdate;
    TextView txtPercentage;
    ProgressBar progressBar;

    boolean finish = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Button
        btnPhoto = (ImageButton) findViewById(R.id.btnPhoto);
        btnVideo = (ImageButton) findViewById(R.id.btnVideo);
        btnCancel = (ImageButton) findViewById(R.id.btnCancel);

        // TextView
        txtChallenge = (TextView) findViewById(R.id.txtChallenge);

        // ImageView
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        // VideoView
        vvVideo = (VideoU) findViewById(R.id.vvVideo);

        // Progress Update
        progressUpdate = (LinearLayout) findViewById(R.id.progressUpdate);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // OnClickListener
        btnPhoto.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        // Init components
        init();

        // Load challenge
        loadChallenge();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_send:
                File file = null;
                if (mCurrentPhotoPath != null) file = new File(mCurrentPhotoPath);
                else if (mCurrentVideoPath != null) file = new File(mCurrentVideoPath);
                if (file != null) {
                    finish = true;
                    HashMap<String, String> dataPost = putDataPost();
                    new DataFileConnect(this, new User(), URLs.UPDATE_CHALLENGE, dataPost, file).execute();
                } else {
                    General.showAlert(this, getString(R.string.title_activity_camera), getString(R.string.camera_no_data), getString(R.string.error_ok));
                }
                break;
            /*
            case R.id.action_post:
                new AlertDialogOptions(this, getString(R.string.title_activity_camera),
                                                getString(R.string.camera_option_post), getString(R.string.YES), getString(R.string.NO));
                break;
                */
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        deleteFiles();
        super.onDestroy();
    }

    @Override
    public void finish() {
        deleteFiles();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        deleteFiles();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        deleteFiles();

        switch (v.getId()) {
            case R.id.btnPhoto:
                deactivateButtons();
                dispatchTakePictureIntent();
                break;
            case R.id.btnVideo:
                deactivateButtons();
                dispatchTakeVideoIntent();
                break;
            case R.id.btnCancel:
                // TODO: refuse the challenge
                break;
        }
    }

    @Override
    public void onPreLoad() {
        progressUpdate.setVisibility(View.VISIBLE);
        // setting progress bar to zero
        progressBar.setProgress(0);
    }

    @Override
    public void onPostLoad(Object[] resultArray, String result) {
        progressUpdate.setVisibility(View.INVISIBLE);
        String error = General.attendErrors(this, result);
        if (error != null) {
            General.showAlert(this, getString(R.string.title_activity_camera), error, getString(R.string.error_ok));
        } else if (finish && result.equals(Errors.ERROR_OK)) {
            finish();
        }
        finish = false;
    }

    @Override
    public void onUpdate(int num) {
        // updating progress bar value
        progressBar.setProgress(num);
        // updating percentage value
        txtPercentage.setText(String.valueOf(num) + "%");
    }

    @Override
    public void clickResponse(boolean response) {
        if (response) postChallenge();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK){
                    deleteVideo();
                    handleBigCameraPhoto();
                }
                break;
            case REQUEST_VIDEO_CAPTURE:
                if (resultCode == RESULT_OK) {
                    deletePhoto();
                    handleBigCameraVideo();
                }
                break;
        }
    }

    /**
     * Method to initialize the action bar.
     */
    private void init() {
        // Overlay ActionBar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        // Data
        user = DataAccess.getUser(this);
        challenge = (Challenge) getIntent().getSerializableExtra("CHALLENGE");
        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#be000000")));
            actionBar.setTitle(challenge.getContact());
        }
    }

    /**
     * Loads the challenge information.
     */
    public void loadChallenge() {
        txtChallenge.setText(Html.fromHtml(challenge.getChallenge()));
    }

    /**
     * Intent to take a picture from the camera.
     */
    private void dispatchTakePictureIntent() {
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = PhotoEditor.createImageFile(this);
            mCurrentPhotoPath = photoFile.getAbsolutePath();
        } catch (IOException ex) {
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Intent photoTaker = new Intent(this, PhotoTakerActivity.class);
            photoTaker.putExtra(Preferences.FILE, mCurrentPhotoPath);
            startActivityForResult(photoTaker, REQUEST_TAKE_PHOTO);
        }
    }

    /**
     * Intent to record a video from the camera.
     */
    private void dispatchTakeVideoIntent() {
        // Create the File where the video should go
        File videoFile = null;
        try {
            videoFile = VideoEditor.createVideoFile(this);
            mCurrentVideoPath = videoFile.getAbsolutePath();
        } catch (IOException e) {
        }
        // Continue only if the File was successfully created
        if (videoFile != null) {
            Intent videoRecorder = new Intent(this, VideoRecorderActivity.class);
            videoRecorder.putExtra(Preferences.FILE, mCurrentVideoPath);
            startActivityForResult(videoRecorder, REQUEST_VIDEO_CAPTURE);
        }
    }

    /**
     * Method to show the image taken by the camera.
     */
    private void handleBigCameraPhoto() {
        ivPhoto.setVisibility(View.VISIBLE);
        vvVideo.setVisibility(View.INVISIBLE);
        PhotoEditor.reduceResolution(mCurrentPhotoPath);
        PhotoEditor.resizePic(ivPhoto, mCurrentPhotoPath);
    }

    /**
     * Method to show the video taken by the camera.
     */
    private void handleBigCameraVideo() {
        ivPhoto.setVisibility(View.INVISIBLE);
        vvVideo.setVisibility(View.VISIBLE);
        if (mCurrentVideoPath != null) vvVideo.setVideoPath(mCurrentVideoPath);
        vvVideo.start();
    }

    /**
     * Deactivate the option buttons.
     */
    private void deactivateButtons() {
        /*
        btnPhoto.setEnabled(false);
        btnVideo.setEnabled(false);
        btnCancel.setEnabled(false);
        */
    }

    /**
     * Discard the current challenge answer and delete the file created
     */
    private void discard() {
        ivPhoto.setVisibility(View.INVISIBLE);
        if (vvVideo.isPlaying()) vvVideo.pause();
        vvVideo.setVisibility(View.INVISIBLE);
        deleteFiles();
    }

    /**
     * Delete the image and video file.
     */
    private void deleteFiles() {
        deletePhoto();
        deleteVideo();
    }

    /**
     * Delete the image file.
     */
    private void deletePhoto() {
        if (mCurrentPhotoPath != null) {
            if (!new File(mCurrentPhotoPath).delete()) {
                Log.e("e", "The photo file has not been deleted.");
            }
            mCurrentPhotoPath = null;
        }
    }

    /**
     * Delete the video file.
     */
    private void deleteVideo() {
        if (mCurrentVideoPath != null) {
            if (!new File(mCurrentVideoPath).delete()) {
                Log.e("e", "The video file has not been deleted.");
            }
            mCurrentVideoPath = null;
        }
    }

    /**
     * Post public challenge.
     */
    private void postChallenge() {
        File filePost = null;
        if (mCurrentPhotoPath != null) filePost = new File(mCurrentPhotoPath);
        else if (mCurrentVideoPath != null) filePost = new File(mCurrentVideoPath);
        if (filePost != null) {
            HashMap<String, String> dataPost = putDataNewPost();
            new DataFileConnect(this, new User(), URLs.NEW_POST, dataPost, filePost).execute();
        } else {
            General.showAlert(this, getString(R.string.title_activity_camera), getString(R.string.camera_no_data), getString(R.string.error_ok));
        }
    }

    /**
     * Returns a HashMap with the information to answer a challenge.
     *
     * @return HashMap
     */
    private HashMap<String, String> putDataPost() {
        HashMap<String, String> data = new HashMap();
        data.put(Preferences.USER_ID, Integer.toString(challenge.getUserId()));
        data.put(Preferences.USERNAME, challenge.getUsername());
        data.put(Preferences.CONTACT_ID, Integer.toString(challenge.getContactId()));
        if (mCurrentPhotoPath != null) {
            data.put(Preferences.TYPE, "P");
        } else if (mCurrentVideoPath != null) {
            data.put(Preferences.TYPE, "V");
        }
        Contact contact = General.getContactFromList(user.getContacts(), challenge.getContactId());
        if (contact != null) data.put(Preferences.NOTIFICATION, contact.getNotification());
        return data;
    }

    /**
     * Returns a HashMap with the information to create a new post.
     *
     * @return HashMap
     */
    private HashMap<String, String> putDataNewPost() {
        HashMap<String, String> data = new HashMap();
        data.put(Preferences.USER_ID, Integer.toString(challenge.getUserId()));
        data.put(Preferences.USERNAME, challenge.getUsername());
        data.put(Preferences.CONTACT_ID, Integer.toString(challenge.getContactId()));
        data.put(Preferences.CONTACT, challenge.getContact());
        data.put(Preferences.CHALLENGE, challenge.getChallenge());
        if (mCurrentPhotoPath != null) {
            data.put(Preferences.TYPE, "P");
        } else if (mCurrentVideoPath != null) {
            data.put(Preferences.TYPE, "V");
        }
        return data;
    }
}
