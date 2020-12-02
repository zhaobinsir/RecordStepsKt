package com.shenxing.recordstepskt;

import android.app.Application;

/**
 * Created by zhaobinsir on 2020/12/2.
 */
public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        StepSensorManger stepSensorManger = StepSensorManger.Companion.getInstances();
        stepSensorManger.initSensor(this);
        stepSensorManger.registerSensor();
    }
}
