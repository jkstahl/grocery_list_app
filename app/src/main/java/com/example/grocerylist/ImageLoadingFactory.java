package com.example.grocerylist;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by neoba on 2/5/2017.
 */

public class ImageLoadingFactory {
    private List<Pair<Integer, Bitmap>> bitmapCache;
    private Context context;
    private int MAX_RECORDS=20;

    public ImageLoadingFactory(Context context) {
        bitmapCache = new LinkedList<>();
        this.context = context;

    }

    private boolean containsKey(Integer key){
        for (Pair<Integer, Bitmap> set : bitmapCache)
            if (set.first == key)
                return true;
        return false;
    }

    private Bitmap get(Integer key){
        for (Pair<Integer, Bitmap> set : bitmapCache)
            if (set.first == key)
                return set.second;
        return null;
    }

    private void put(Integer key, Bitmap bitmap) {
        if (bitmapCache.size() >= MAX_RECORDS) {
            bitmapCache.remove(0);
        }
        bitmapCache.add(new Pair<Integer, Bitmap>(key, bitmap));
    }

    public void loadBitmap(int key, Cursor c, int imageIndex, ImageView view) {
        if (containsKey(key)) {
            view.setImageBitmap(get(key));
        } else { // load loading image and set async task to get the real image.
            view.setImageBitmap(DbBitmapUtility.getLoadingImage(context));
            BitmapWorkerTask wt = new BitmapWorkerTask(view, context, this);
            Pair<Integer, byte[]> param = new Pair<Integer, byte[]>(key, c.getBlob(imageIndex));
                        wt.execute(param);
        }

    }

    public void addToCache(int key, Bitmap image) {

        put(key, image);
    }

    private class ImageCacheObject {
        private long position=0;
        private Bitmap image;


    }

}
