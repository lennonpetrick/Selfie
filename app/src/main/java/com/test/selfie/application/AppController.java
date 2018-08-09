package com.test.selfie.application;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    private static AppController mInstance;

    private RequestQueue mRequestQueue,
                         mImageRequestQueue;
    private ImageLoader mImageLoader;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }

        return mRequestQueue;
    }

    private RequestQueue getImageRequestQueue() {
        if (mImageRequestQueue == null) {
            mImageRequestQueue = Volley.newRequestQueue(this);
        }

        return mImageRequestQueue;
    }

    public ImageLoader getImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(getImageRequestQueue(), new LruBitmapCache());
        }

        return mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests() {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }

        if (mImageRequestQueue != null) {
            mImageRequestQueue.cancelAll(TAG);
        }
    }
}
