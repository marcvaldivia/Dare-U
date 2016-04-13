package com.dare_u.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class PhotoEditor {

    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    /**
     * Create a file to store the image from the camera.
     *
     * @param context
     * @return File
     * @throws IOException
     */
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String imageFileName = JPEG_FILE_PREFIX + "DARE_U_TEMP" + JPEG_FILE_SUFFIX;
        File image = new File(context.getFilesDir(), imageFileName);
        return image;
    }

    /**
     * Resize the image in the path to the dimensions of the imageView.
     *
     * @param ivPhoto
     * @param mCurrentPhotoPath
     * @return Bitmap
     */
    public static Bitmap resizePic(ImageView ivPhoto, String mCurrentPhotoPath) {

		// Get the size of the ImageView
        int targetW = ivPhoto.getWidth();
        int targetH = ivPhoto.getHeight();

		// Get the size of the image
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Rotate image
        boolean rotate = photoW >= photoH;

		// Figure out which way needs to be reduced less
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		// Set bitmap options to scale the image decode target
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

		// Decode the JPEG file into a Bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        if (rotate) {
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }

        // Set the bitmap in the imageView
        ivPhoto.setImageBitmap(bitmap);

        return bitmap;
    }

    /**
     * Reduce the resolution of the image file sent by the parameter.
     *
     * @param file
     */
    public static void reduceResolution(String file) {
        int MAX_IMAGE_SIZE = 100 * 1024 * 1024; // max final file size
        Bitmap bmpPic = BitmapFactory.decodeFile(file);
        if ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
            BitmapFactory.Options bmpOptions = new BitmapFactory.Options();
            bmpOptions.inSampleSize = 1;
            while ((bmpPic.getWidth() >= 1024) && (bmpPic.getHeight() >= 1024)) {
                bmpOptions.inSampleSize++;
                bmpPic = BitmapFactory.decodeFile(file, bmpOptions);
            }
        }
        int compressQuality = 104; // quality decreasing by 5 every loop. (start from 99)
        int streamLength = MAX_IMAGE_SIZE;
        while (streamLength >= MAX_IMAGE_SIZE) {
            ByteArrayOutputStream bmpStream = new ByteArrayOutputStream();
            compressQuality -= 5;
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream);
            byte[] bmpPicByteArray = bmpStream.toByteArray();
            streamLength = bmpPicByteArray.length;
        }
        try {
            FileOutputStream bmpFile = new FileOutputStream(file);
            bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile);
            bmpFile.flush();
            bmpFile.close();
        } catch (Exception e) {
        }
    }

}
