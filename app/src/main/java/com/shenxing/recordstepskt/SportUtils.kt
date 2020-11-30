package com.shenxing.recordstepskt

/**
 *  Created by zhaobinsir
 *  on 2020/11/30.
 *  运动工具类
 */

//默认一步距离 单位、m
var stepsDistances=0.7

//默认体重 单位、kg
var bodyWeight=75

//运动系数 热量（kcal）＝体重（kg）×距离（公里）×运动系数
/* 健走：k=0.8214
*
* 跑步：k=1.036
*
* 自行车：k=0.6142
*
* 轮滑、溜冰：k=0.518
*
* 室外滑雪：k=0.888
* **/
 private const val walk=0.8214

 private const val run=1.036

 private const val bike=0.6142

 private const val iceSkate=0.518

 private const val outSkate=0.888

 var sportTag=walk

/**
 * 获取公里路程
 * **/
  fun getKilometreBySteps(steps: Int): Double {
    return steps.times(stepsDistances)
  }

/**
 * 获取卡路里
 * **/
 fun getCaloriesBySteps(kilometre: Double): Double {
    return bodyWeight.times(kilometre).times(walk)
}