package com.example.grocerylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by neoba on 1/14/2017.
 */

public class DbBitmapUtility {

    public static int IMAGE_HIEGHT=1000;
    public static int IMAGE_WIDTH=1000;

    public static int THUMBNAIL_HEIGHT=300;
    public static int THUMBNAIL_WIDTH=300;

    public static Bitmap getBitmap(Context context, Bitmap image, String resource) {
        Matrix matrix = new Matrix();
        ExifInterface exifReader;
        //Bitmap bmp_default;
        /*
        try {
            //bmp_default = BitmapFactory.decodeFile(image);
            exifReader = new ExifInterface(resource);
        } catch (IOException e) {
            Log.e("bitmaputility", e.toString());
            return image;
        }

        int orientation = exifReader.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, -1);

        if (orientation == ExifInterface.ORIENTATION_NORMAL) {
            Log.e("normal", "normal");
            // Do nothing. The original image is fine.
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            Log.e("rotate_90", "rotate_90");
            matrix.postRotate(90);

        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            Log.e("rotate_180", "rotate_180");
            matrix.postRotate(180);

        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            Log.e("rotate_270", "rotate_270");
            matrix.postRotate(270);

        }*/


        Bitmap bm = Bitmap.createBitmap(image, 0, 0, IMAGE_WIDTH, IMAGE_WIDTH, matrix, false);

        return bm;
    }

    // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(Context context, byte[] image) {

            if (image == null || image.length <= 1)
                return BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image_box);
            else
                return BitmapFactory.decodeByteArray(image, 0, image.length);
        }


}
