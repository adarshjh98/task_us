package com.steed.top5.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageUtil {
    private static String TAG = "ImageUtil";
    //Maintain as aspect ratio of 4:3
    public static final int BITMAP_WIDTH = 450;
    public static final int BITMAP_HEIGHT = 600;

    public static String convertBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);

        // Log.i("ImageUtil",encoded);

        //System.out.print(encoded);


        return encoded;


    }

    /**
     * Figure out what ratio we can load our image into memory at while still being bigger than
     * our desired width and height
     *
     * @param srcWidth
     * @param srcHeight
     * @param dstWidth
     * @param dstHeight
     * @return
     */
    public static int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        final float srcAspect = (float) srcWidth / (float) srcHeight;
        final float dstAspect = (float) dstWidth / (float) dstHeight;

        if (srcAspect > dstAspect) {
            return srcWidth / dstWidth;
        } else {
            return srcHeight / dstHeight;
        }
    }

    /**
     * Maintain the aspect ratio so the resulting image does not look smooshed
     *
     * @param origWidth
     * @param origHeight
     * @return
     */
    public static int[] calculateAspectRatio(int origWidth, int origHeight) {
        int newWidth = BITMAP_WIDTH;
        int newHeight = BITMAP_HEIGHT;

        // If no new width or height were specified return the original bitmap
        if (newWidth <= 0 && newHeight <= 0) {
            newWidth = origWidth;
            newHeight = origHeight;
        }
        // Only the width was specified
        else if (newWidth > 0 && newHeight <= 0) {
            newHeight = (int) ((newWidth / (double) origWidth) * origHeight);
        }
        // only the height was specified
        else if (newWidth <= 0 && newHeight > 0) {
            newWidth = (int) ((newHeight / (double) origHeight) * origWidth);
        }
        // If the user specified both a positive width and height
        // (potentially different aspect ratio) then the width or height is
        // scaled so that the image fits while maintaining aspect ratio.
        // Alternatively, the specified width and height could have been
        // kept and Bitmap.SCALE_TO_FIT specified when scaling, but this
        // would result in whitespace in the new image.
        else {
            double newRatio = newWidth / (double) newHeight;
            double origRatio = origWidth / (double) origHeight;

            Log.i(TAG,"Original aspect ration "+origRatio);
            Log.i(TAG, "Original Width "+origWidth);
            Log.i(TAG, "Original Height "+origHeight);

            if (origRatio > newRatio) {
                newHeight = (newWidth * origHeight) / origWidth;
            } else if (origRatio < newRatio) {
                newWidth = (newHeight * origWidth) / origHeight;
            }
        }

        int[] retval = new int[2];
        retval[0] = newWidth;
        retval[1] = newHeight;
        return retval;
    }

    //http://www.thaicreate.com/mobile/android-photo-camera-resize-image.html
    public static Bitmap decodeFile(File file) {
        Bitmap rotatedBitmap = null;
        try {


            final String absolutePath = file.getAbsolutePath();

            /*

            Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            */

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(file), null, options);


            ExifInterface ei = new ExifInterface(absolutePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);


            int rotatedWidth = options.outWidth;
            int rotatedHeight = options.outHeight;
            boolean rotated = false;
            int degree = 0;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    //rotatedBitmap = rotateImage(bitmap, 90);
                    degree = 90;

                    rotatedWidth = options.outHeight;
                    rotatedHeight = options.outWidth;
                    rotated = true;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    //rotatedBitmap = rotateImage(bitmap, 270);
                    degree = 270;

                    rotatedWidth = options.outHeight;
                    rotatedHeight = options.outWidth;
                    rotated = true;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;

                    rotatedWidth = options.outHeight;
                    rotatedHeight = options.outWidth;
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedWidth = options.outWidth;
                    rotatedHeight = options.outHeight;
            }

            Log.d(TAG, "Rotated Width "+rotatedWidth);
            Log.d(TAG, "Rotated Height "+rotatedHeight);

            int[] widthHeight = calculateAspectRatio(rotatedWidth, rotatedHeight);



            int sampleSize = calculateSampleSize(rotatedWidth, rotatedHeight, widthHeight[0], widthHeight[1]);

            //int sampleSize =1;
                    // Load in the smallest bitmap possible that is closest to the size we want
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;

            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file), null, options);


            int scaledWidth = (!rotated) ? widthHeight[0] : widthHeight[1];
            int scaledHeight = (!rotated) ? widthHeight[1] : widthHeight[0];


//
//            int scaledWidth =BITMAP_WIDTH;
//            int scaledHeight=BITMAP_HEIGHT;


            Log.i(TAG, "scaledWidth " + scaledWidth);
            Log.i(TAG, "sample size" + sampleSize);
            Log.i(TAG, "scaledHeight " + scaledHeight);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);

            /*
            if (degree == 0) {
                rotatedBitmap = scaledBitmap;
            } else {
                rotatedBitmap = rotateImage(scaledBitmap, degree);
            }*/

            if ((degree != 0)) {
                Matrix matrix = new Matrix();
                matrix.setRotate(degree);
                try {
                    rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                } catch (OutOfMemoryError oom) {
                    oom.printStackTrace();
                }
            }else{
                rotatedBitmap = scaledBitmap;
            }


        } catch (IOException e) {
        }
        return rotatedBitmap;
    }


    //https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}

