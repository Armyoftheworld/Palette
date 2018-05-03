package com.juziwl.palette;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * @author Army
 * @version V_1.0.0
 * @date 2018/5/3
 * @description
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Global.application = this;
        Logger.init("palette")
                .methodCount(1)
                .hideThreadInfo()
                .logLevel(BuildConfig.DEBUG ? LogLevel.FULL : LogLevel.NONE);
    }
}
