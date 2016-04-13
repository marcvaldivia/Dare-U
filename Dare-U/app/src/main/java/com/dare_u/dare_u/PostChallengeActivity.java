package com.dare_u.dare_u;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dare_u.behaviour.ConnectFileActivity;
import com.dare_u.camera.PhotoEditor;
import com.dare_u.camera.VideoEditor;
import com.dare_u.connect.DataFileDownload;
import com.dare_u.constants.Errors;
import com.dare_u.constants.Preferences;
import com.dare_u.constants.URLs;
import com.dare_u.domain.Post;
import com.dare_u.objects.VideoU;
import com.dare_u.utils.General;

import java.io.File;
import java.util.HashMap;

public class PostChallengeActivity extends AppCompatActivity implements ConnectFileActivity {

    Post post;
    File file;

    ImageView ivPhoto;
    VideoU vvVideo;
    TextView txtChallenge;

    LinearLayout progressUpdate;
    TextView txtPercentage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_challenge);

        // ImageView
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        // VideoView
        vvVideo = (VideoU) findViewById(R.id.vvVideo);

        // TextView
        txtChallenge = (TextView) findViewById(R.id.txtChallenge);

        // Progress Update
        progressUpdate = (LinearLayout) findViewById(R.id.progressUpdate);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        // Init components
        init();

        // load Challenge
        loadPost();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simple, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        deleteFile();
        super.onDestroy();
    }

    @Override
    public void finish() {
        deleteFile();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        deleteFile();
        super.onBackPressed();
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
            General.showAlert(this, getString(R.string.title_activity_contact_challenge), error, getString(R.string.error_ok));
        } else if (result.equals(Errors.ERROR_OK)) {
            if (post.getType() == 'P') {
                ivPhoto.setVisibility(View.VISIBLE);
                Bitmap bitmap = PhotoEditor.resizePic(ivPhoto, file.getAbsolutePath());
                ivPhoto.setImageBitmap(bitmap);
            } else {
                vvVideo.setVisibility(View.VISIBLE);
                vvVideo.setVideoURI(Uri.fromFile(file));
                vvVideo.start();
            }
        }
    }

    @Override
    public void onUpdate(int num) {
        // updating progress bar value
        progressBar.setProgress(num);
        // updating percentage value
        txtPercentage.setText(String.valueOf(num) + "%");
    }

    /**
     * Method to initialize the action bar.
     */
    private void init() {
        // Overlay ActionBar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        // Data
        post = (Post) getIntent().getSerializableExtra("POST");
        // Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#be000000")));
            actionBar.setTitle(post.getUsername());
        }
    }

    /**
     * Loads the challenge information.
     */
    private void loadPost() {
        txtChallenge.setText(Html.fromHtml(post.getChallenge()));
        HashMap<String, String> dataPost = new HashMap();
        dataPost.put(Preferences.FILE, post.getUrl());
        try {
            if (post.getType() == 'P') {
                file = PhotoEditor.createImageFile(this);
            } else {
                file = VideoEditor.createVideoFile(this);
            }
            new DataFileDownload(this, new Post(), URLs.GET_FILE, dataPost, file).execute();
        } catch (Exception e) {
            General.showAlert(this, getString(R.string.title_activity_post_challenge), getString(R.string.error_GEN), getString(R.string.error_ok));
        }
    }

    /**
     * Delete the image file.
     */
    private void deleteFile() {
        if (file.getAbsoluteFile() != null) {
            if (!file.delete()) {
                Log.e("e", "The photo file has not been deleted.");
            }
        }
    }

}
