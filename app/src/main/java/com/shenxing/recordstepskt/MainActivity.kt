package com.shenxing.recordstepskt

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.opengl.Visibility
import android.os.*
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log


class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    val simpleDateFormat: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    var nowSteps = 0f//当前步数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPermission()
        initView()
        initListener()
        //加入系统白名单
        saveLife()
    }

    private fun saveLife() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG,"isWhiteList ${isBatteryOptimizations()}")
            if (!isBatteryOptimizations()) {
                requestBatteryOptimizations()
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun isBatteryOptimizations() :Boolean{//是否加入白名单
        val isIgnoring :Boolean
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName)
        return isIgnoring
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun requestBatteryOptimizations() {//请求白名单权限
        startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        })
    }


    //只有大于>=android 10 才去请求权限, 请求获取运动权限
    private fun initPermission() {
        val pCode =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
        Log.d(
            TAG,
            "permission_code $pCode hasPermission=${pCode == PackageManager.PERMISSION_GRANTED}"
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)//大于等于android 10
        {
            if (pCode == PackageManager.PERMISSION_DENIED) {//为获取权限
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 10
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        //View init
        tvDetector.text = "支持STEP_DETECTOR ${StepSensorManger.instances.hasDetectorPermission()}"
        tvCounter.text = "支持STEP_COUNTER ${StepSensorManger.instances.hasCounterPermission()}"
    }

    @SuppressLint("SetTextI18n")
    private fun initListener() {
        //switch
        modeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                tvMode.text = "精确模式"
                tvAllSteps.visibility = View.GONE
                tvSingleStep.visibility = View.VISIBLE
                tvStepTime.visibility = View.VISIBLE
            } else {
                tvMode.text = "标准模式"
                tvAllSteps.visibility = View.VISIBLE
                tvSingleStep.visibility = View.GONE
                tvStepTime.visibility = View.GONE
            }
        }

        StepSensorManger.instances.stepSensorListener=object : StepSensorManger.SensorListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.e(TAG, sensor?.name + "---" + accuracy)
            }

            override fun onSensorChanged(event: SensorEvent?) {
                 val str =
                        "event.values ${event!!.values[0]}---event.accuracy ${event.accuracy}---event.timestamp ${event.timestamp}"
                    tvLog.text = str
                    Log.e(TAG, str)
                    tvAllSteps.text = ("总步伐计数:${event.values[0]}")
            }

        }

        StepSensorManger.instances.countSensorListener=object : StepSensorManger.SensorListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                Log.e(TAG, "name $sensor?.name--accuracy $accuracy")
            }

            override fun onSensorChanged(event: SensorEvent?) {
                val str =
                   "event.values ${event!!.values[0]}---event.accuracy ${event.accuracy}---event.timestamp ${event.timestamp}"
               tvLog.text = str
               Log.e(TAG, str)
               nowSteps+=event.values[0]
               tvSingleStep.text = "当前步伐计数:${nowSteps}"
               tvStepTime.text =
                   "当前步伐时间:${simpleDateFormat.format(Date(System.currentTimeMillis()))}"
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult $requestCode")
        Log.d(TAG, "grantResults_${grantResults[0]==PackageManager.PERMISSION_GRANTED}")
    }


    override fun onDestroy() {
        super.onDestroy()
//        StepSensorManger.instances.unregisterSensor()
    }
}
