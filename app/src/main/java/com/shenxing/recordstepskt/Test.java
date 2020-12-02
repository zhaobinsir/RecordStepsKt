package com.shenxing.recordstepskt;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * Created by zhaobinsir
 * on 2020/11/30.
 */
public class Test extends AppCompatActivity {

    private  StepSensorManger manger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manger=  StepSensorManger.Companion.getInstances();
        //总步数发生改变时回调
        manger.setCountSensorListener(new StepSensorManger.SensorListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {//event[0] 就是总步数

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {//精度发生改变后的回调

            }
        });
        //当前步数发生改变时回调
        manger.setStepSensorListener(new StepSensorManger.SensorListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manger.unregisterSensor();
    }

    //卡路里计算
    /**
     * 健走：k=0.8214
     *
     * 跑步：k=1.036
     *
     * 自行车：k=0.6142
     *
     * 轮滑、溜冰：k=0.518
     *
     * 室外滑雪：k=0.888
     * **/
    //跑步热量（kcal）＝体重（kg）×距离（公里）×运动系数
    //calories = weight * distance * 0.8214;
}
