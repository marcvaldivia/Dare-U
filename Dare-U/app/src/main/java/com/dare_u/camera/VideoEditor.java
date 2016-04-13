package com.dare_u.camera;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class VideoEditor {

    private static final String MP4_FILE_PREFIX = "VID_";
    private static final String MP4_FILE_SUFFIX = ".mp4";

    /**
     * Create a file to store the video from the camera.
     *
     * @param context
     * @return File
     * @throws IOException
     */
    public static File createVideoFile(Context context) throws IOException {
        // Create an image file name
        String videoFileName = MP4_FILE_PREFIX + "DARE_U_TEMP" + MP4_FILE_SUFFIX;
        File video = new File(context.getFilesDir(), videoFileName);
        return video;
    }

}
