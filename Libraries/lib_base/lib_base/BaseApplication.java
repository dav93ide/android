package com.bodhitech.it.lib_base.lib_base;

import android.app.Application;

import com.fondesa.lyra.Lyra;

public class BaseApplication extends Application {

    /** Override Lifecycle Methods **/
    @Override
    public void onCreate() {
        super.onCreate();
        Lyra.with(this).build();
    }

}
