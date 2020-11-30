package com.shenxing.recordstepskt

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

/**
 *  Created by zhaobinsir
 *  on 2020/11/30.
 *  计步管理类
 */
class StepSensorManger private constructor() {

    private val TAG = "StepSensorManger"

    companion object {
        val instances = InnerClass.holder
    }

    private object InnerClass {
        val holder = StepSensorManger()
    }

    interface SensorListener : SensorEventListener

    var stepSensorListener: SensorListener? = null //用于对外接口回调  单次步伐传感器
    var countSensorListener: SensorListener? = null//用于对外接口回调 步伐总数传感器

    private var context: Application? = null//Application Context
    private var sensorManager: SensorManager? = null  //传感器
    private var stepCounter: Sensor? = null    //步伐总数传感器
    private var stepDetector: Sensor? = null    //单次步伐传感器
    private var stepCounterListener: //内部使用，步伐总数传感器事件监听器
            SensorEventListener? = null
    private var stepDetectorListener: //内部使用，单次步伐传感器事件监听器
            SensorEventListener? = null

    /**
     * 初始化传感器服务
     * @param context
     */
    fun initSensor(context: Application) {
        sensorManager = context.getSystemService(AppCompatActivity.SENSOR_SERVICE) as SensorManager?
        stepCounter = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);//获取计步总数传感器
        stepDetector = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);//获取单次计步传感器
        this.context = context
    }


    /**
     * 注册
     *
     */
    fun registerSensor() {
        initListener()
        if (hasDetectorPermission()!!) {
            sensorManager?.registerListener(
                stepDetectorListener,
                stepDetector,
                SensorManager.SENSOR_DELAY_FASTEST
            )
            Log.e(TAG, "register_Detector")
        }
        if (hasCounterPermission()!!) {
            sensorManager?.registerListener(
                stepCounterListener,
                stepCounter,
                SensorManager.SENSOR_DELAY_FASTEST
            )
            Log.e(TAG, "register_Counter")
        }
    }

    /**
     * 初始化监听
     */
    private fun initListener() {
        stepCounterListener = object : SensorEventListener {
            //单步监听
            override fun onSensorChanged(event: SensorEvent) {
                stepSensorListener?.onSensorChanged(event)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.e(TAG, sensor.name + "---" + accuracy)
                stepSensorListener?.onAccuracyChanged(sensor, accuracy)
            }
        }
        stepDetectorListener = object : SensorEventListener {
            //总步数监听
            override fun onSensorChanged(event: SensorEvent?) {
                countSensorListener?.onSensorChanged(event)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.e(TAG, "name $sensor?.name--accuracy $accuracy")
                countSensorListener?.onAccuracyChanged(sensor, accuracy)
            }
        }
    }

    /**
     *反注册
     */
     fun unregisterSensor() {
        if (hasDetectorPermission()!!) {
            sensorManager?.unregisterListener(stepDetectorListener)
        }
        if (hasCounterPermission()!!) {
            sensorManager?.unregisterListener(stepCounterListener)
        }
    }


    /**
     *是否支持记步传感器
     */
    fun hasCounterPermission() =
        context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)

    /**
     *是否支持单步传感器
     */
    fun hasDetectorPermission() =
        context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)


}