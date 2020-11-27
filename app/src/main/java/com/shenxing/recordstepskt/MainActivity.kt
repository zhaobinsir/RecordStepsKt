package com.shenxing.recordstepskt

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    val TAG="MainActivity"

    val simpleDateFormat: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    var sensorManager: SensorManager? = null  //传感器
    var stepCounter: Sensor? = null    //步伐总数传感器
    var stepDetector: Sensor? = null    //单次步伐传感器
    private var stepCounterListener: //步伐总数传感器事件监听器
            SensorEventListener? = null
    private var stepDetectorListener: //单次步伐传感器事件监听器
            SensorEventListener? = null

    var nowSteps=0f//当前步数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        registerSensor()
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        stepCounter = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);//获取计步总数传感器
        stepDetector = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);//获取单次计步传感器
        //View init
        tvDetector.text="支持STEP_DETECTOR ${hasDetectorPermission()}"
        tvCounter.text="支持STEP_COUNTER ${hasCounterPermission()}"
    }
    @SuppressLint("SetTextI18n")
    private fun initListener() {
        //switch
        modeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tvMode.text="精确模式"
                tvAllSteps.visibility= View.GONE
                tvSingleStep.visibility=View.VISIBLE
                tvStepTime.visibility=View.VISIBLE
            }else{
                tvMode.text="标准模式"
                tvAllSteps.visibility= View.VISIBLE
                tvSingleStep.visibility=View.GONE
                tvStepTime.visibility=View.GONE
            }
        }
        stepCounterListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val str="event.values ${event!!.values[0]}---event.accuracy ${event.accuracy}---event.timestamp ${event.timestamp}"
                tvLog.text=str
                Log.e(TAG, str)
                tvAllSteps.text=("总步伐计数:${event.values[0]}")
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Log.e(TAG, sensor.name + "---" + accuracy)
            }
        }
        stepDetectorListener=object :SensorEventListener{
            override fun onSensorChanged(event: SensorEvent?) {
                val str="event.values ${event!!.values[0]}---event.accuracy ${event.accuracy}---event.timestamp ${event.timestamp}"
                tvLog.text=str
                Log.e(TAG, str)
                nowSteps+=event.values[0]
                tvSingleStep.text="当前步伐计数:${nowSteps}"
                tvStepTime.text="当前步伐时间:${simpleDateFormat.format(Date(System.currentTimeMillis()))}"
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.e(TAG,"name $sensor?.name--accuracy $accuracy")
            }
        }
    }

    //是否支持记步传感器
    private fun hasCounterPermission() =
        packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)

    //是否支持单步传感器
    private fun hasDetectorPermission() =
        packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR)

    //注册
    private fun registerSensor() {
        if (hasDetectorPermission()) {
            sensorManager?.registerListener(stepDetectorListener, stepDetector, SensorManager.SENSOR_DELAY_FASTEST)
            Log.e(TAG,"register_Detector")
        }
        if (hasCounterPermission()) {
            sensorManager?.registerListener(stepCounterListener,stepCounter,SensorManager.SENSOR_DELAY_FASTEST)
            Log.e(TAG,"register_Counter")
        }
    }

    //反注册
  private  fun unregisterSensor() {
        if (!hasDetectorPermission()) {
            sensorManager?.unregisterListener(stepDetectorListener)
        }
        if (!hasCounterPermission()) {
            sensorManager?.unregisterListener(stepCounterListener)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterSensor()
    }
}
