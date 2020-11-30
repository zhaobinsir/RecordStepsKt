package com.shenxing.recordstepskt

import android.app.Application

/**
 *  Created by zhaobinsir
 *  on 2020/11/30.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        StepSensorManger.instances.let {
            it.initSensor(this)
            it.registerSensor()
        }
    }
}