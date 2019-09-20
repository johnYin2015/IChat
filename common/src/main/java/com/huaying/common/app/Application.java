package com.huaying.common.app;

import android.os.SystemClock;

import java.io.File;

/**
 * @authoer johnyin2015
 */
public class Application extends android.app.Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 获取缓存文件夹地址
     * @return 缓存文件夹地址
     */
    public static File getCacheDirFile(){
        return instance.getCacheDir();
    }

    public static File getPortraitTmpFile(){
        File dir = new File(getCacheDirFile(),"portrait");
        dir.mkdirs();

        File[] files = dir.listFiles();
        if (files!=null&&files.length>0){
            for (File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }

        File path = new File(dir, SystemClock.uptimeMillis()+".jpg");
        return path;
    }
}
