package com.example.grocerylist;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;

import android.support.v4.util.Pair;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by neoba on 2/5/2017.
 */

class BitmapWorkerTask extends AsyncTask<Pair<Integer, byte[]>, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private Context context;
    private ImageLoadingFactory imageCache;
    private  Pair<Integer, byte[]> data;

    public BitmapWorkerTask(ImageView imageView, Context context, ImageLoadingFactory imageCache) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;
        this.imageCache = imageCache;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Pair<Integer, byte[]>... params) {
        data = params[0];
        Bitmap imageBitmap = DbBitmapUtility.getImage(context, data.second);
        return ThumbnailUtils.extractThumbnail(imageBitmap, DbBitmapUtility.THUMBNAIL_WIDTH, DbBitmapUtility.THUMBNAIL_HEIGHT);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
            imageCache.addToCache(data.first, bitmap);
        }
    }
}
