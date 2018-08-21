package com.test.selfie.shared.schedulers;

import io.reactivex.Scheduler;

public interface BaseSchedulers {

    Scheduler io();

    Scheduler mainThread();

}
