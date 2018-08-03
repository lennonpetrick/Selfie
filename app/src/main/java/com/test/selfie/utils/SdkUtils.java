package com.test.selfie.utils;

import android.os.Build;

public class SdkUtils {

    public static boolean isNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

}
