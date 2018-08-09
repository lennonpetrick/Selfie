package com.test.selfie.shared;

import com.google.gson.Gson;

public class GsonSingleton {

    private static GsonSingleton mInstance;
    private Gson mGson;

    private GsonSingleton() {
        this.mGson = new Gson();
    }

    public static Gson getGson() {
        if (mInstance == null) {
            mInstance = new GsonSingleton();
        }
        return mInstance.mGson;
    }

}
