package com.huaying.italker.factory;

import com.huaying.italker.common.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @authoer johnyin2015
 */
public class Factory {
    private static final Factory instance;

    private final Executor executor;

    static {
        instance=new Factory();
    }

    private Factory(){
        executor = Executors.newFixedThreadPool(4);
    }

    //大项目 代码量
    public static void runAsync(Runnable runnable){
        instance.executor.execute(runnable);
    }

    /**
     * 返回全局的Application
     *
     * @return Application
     */
    public static Application app() {
        return Application.getInstance();
    }
}
