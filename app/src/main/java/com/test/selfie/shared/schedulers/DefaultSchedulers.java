package com.test.selfie.shared.schedulers;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DefaultSchedulers implements BaseSchedulers {

    private static BaseSchedulers mInstance;

    public static synchronized BaseSchedulers getInstance() {
        if (mInstance == null) {
            mInstance = new DefaultSchedulers();
        }

        return mInstance;
    }

    private DefaultSchedulers() {}

    @NonNull
    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @NonNull
    @Override
    public Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }
}
