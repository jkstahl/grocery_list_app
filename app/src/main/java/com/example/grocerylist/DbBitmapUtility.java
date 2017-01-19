package com.example.grocerylist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by neoba on 1/14/2017.
 */

public class DbBitmapUtility {

    public static int IMAGE_HIEGHT=1000;
    public static int IMAGE_WIDTH=1000;

    public static int THUMBNAIL_HEIGHT=300;
    public static int THUMBNAIL_WIDTH=300;

    // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }


}
