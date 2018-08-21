package com.test.selfie.shared.schedulers;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class TestSchedulers implements BaseSchedulers {

    private static BaseSchedulers mInstance;

    public static synchronized BaseSchedulers getInstance() {
        if (mInstance == null) {
            mInstance = new TestSchedulers();
        }

        return mInstance;
    }

    private TestSchedulers() {}

    @NonNull
    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @NonNull
    @Override
    public Scheduler mainThread() {
        return Schedulers.trampoline();
    }
}

