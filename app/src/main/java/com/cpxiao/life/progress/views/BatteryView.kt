package com.cpxiao.life.progress.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.cpxiao.lib.Config
import java.util.*

/**
 * @author cpxiao on 2017/06/22.
 */
class BatteryView : View {
    val DEBUG = Config.Log.DEBUG
    val TAG = "BatteryView"

    val DATA_SIZE_MAX = 6
    var showIndex = -1
    var deltaYear = 0

    //平均寿命
    var lifeExpectancy = (71.4 * 365).toInt()
    //数据格式20170129
    val mDateList = ArrayList<Long>()
    //画笔
    val mPaint: Paint = Paint()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        initPaint()
    }

    fun initPaint() {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.color = 0xff44b39a.toInt()
//        mPaint.strokeWidth = 10f
        mPaint.textSize = 100f
        mPaint.textAlign = Paint.Align.CENTER
    }

    fun getBatteryCount(): Int {
        return mDateList.size
    }

    fun getSubTitle(): String {
//        if (mDateList.size <= 0) {
        return ""
//        }
//        return String.format(context.getString(R.string.sub_title), deltaYear.toString(), (mDateList[showIndex] / 10000).toString())
    }

    /**
     * 检查此日期是否已在列表中
     */
    fun checkUniqueness(data: Long): Boolean {
        if (mDateList.size <= 0) {
            return true
        }
        return !mDateList.contains(data)
    }

    /**
     * 添加日期
     */
    fun addBattery(date: Long) {
        if (DEBUG) {
            Log.d(TAG, "addBattery -> date = " + date)
        }
        if (checkUniqueness(date)) {
            mDateList.add(0, date)
            showIndex = 0
        }
        //若超过最大值，则把最先插入的值删除
        if (mDateList.size > DATA_SIZE_MAX) {
            mDateList.removeAt(DATA_SIZE_MAX - 1)
        }
        invalidate()
    }

    /**
     * 清除数据
     */
    fun clearBattery() {
        mDateList.clear()
        showIndex = -1
        invalidate()
    }
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val width = getMeasuredWidth()
//        setMeasuredDimension(width, width)
//    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mDateList.size <= 0) {
            myDraw(canvas, 0)
            return
        }

        myDraw(canvas, mDateList[showIndex])
    }

    fun getDeltaDays(date: Long): Int {
        val calendar = Calendar.getInstance()
        val today = calendar.timeInMillis
        if (DEBUG) {
            Log.d(TAG, "getProgress -> today = " + calendar.timeInMillis)
        }
        calendar.set((date / 10000).toInt(), ((date / 100) % 100 - 1).toInt(), (date % 100).toInt())
        if (DEBUG) {
            Log.d(TAG, "getProgress -> dateStart = " + calendar.timeInMillis)
        }
        val dateStart = calendar.timeInMillis
        val deltaDays = ((today - dateStart) / 86400000F).toInt()
        if (DEBUG) {
            Log.d(TAG, "(today - dateStart) / 86400000 = " + ((today - dateStart) / 86400000))
        }
        deltaYear = deltaDays / 365
        return deltaDays
    }

    fun getProgress(date: Long): Float {
        if (DEBUG) {
            Log.d(TAG, "getProgress -> date = " + date)
        }
        if (showIndex < 0) {
            return 0f
        }

        val progress = 1F * getDeltaDays(date) / lifeExpectancy

        return progress
    }

    fun myDraw(canvas: Canvas?, date: Long) {
        val progress = getProgress(date)
        drawBattery(canvas, progress)
        drawProgressText(canvas, progress)
        drawIndex(canvas)
    }

    var batteryW = width
    var batteryH = height

    fun drawBattery(canvas: Canvas?, progress: Float) {
        batteryH = (0.8 * height).toInt()
        batteryW = (0.5 * batteryH).toInt()

        val rx = 0.05f * batteryW


        val rectF = RectF()
        rectF.left = (width - batteryW) / 2f
        rectF.top = (height - batteryH) / 2f
        rectF.right = (width + batteryW) / 2f
        rectF.bottom = (height + batteryH) / 2f
        mPaint.color = Color.GRAY
        canvas?.drawRoundRect(rectF, rx, rx, mPaint)

        val rectFTop = RectF()
        val rectFTopW = 0.382f * batteryW
        val rectFTopH = 0.2f * rectFTopW
        val rectFTopRX = 0.05F * rectFTopW
        rectFTop.left = (width - rectFTopW) / 2f
        rectFTop.top = rectF.top - rectFTopH
        rectFTop.right = (width + rectFTopW) / 2f
        rectFTop.bottom = rectF.top + rectFTopH
        canvas?.drawRoundRect(rectFTop, rectFTopRX, rectFTopRX, mPaint)

        val progressRectF = RectF()
        progressRectF.left = (width - batteryW) / 2f
        progressRectF.top = (height - batteryH) / 2f + batteryH * progress
        progressRectF.right = (width + batteryW) / 2f
        progressRectF.bottom = (height + batteryH) / 2f
        mPaint.color = Color.GREEN

        canvas?.drawRoundRect(progressRectF, rx, rx, mPaint)

    }

    fun drawProgressText(canvas: Canvas?, progress: Float) {
        val msg = (100F - 100F * progress).toString()
        val show = msg.substring(0, Math.min(4, msg.length)) + "%"
        mPaint.color = Color.BLACK
        mPaint.textSize = (0.15 * batteryW).toFloat()
        canvas?.drawText(show, width.toFloat() / 2, height.toFloat() / 2, mPaint)
    }


    fun drawIndex(canvas: Canvas?) {
        if (mDateList.size <= 0) {
            return
        }
        val pointY = height * 0.95f
        val pointR = batteryW / (DATA_SIZE_MAX + 1)
        val indexPaddingLeft = (width - (mDateList.size - 1) * pointR) / 2f
        for (index in mDateList.indices) {
            if (index == showIndex) {
                mPaint.color = Color.WHITE
            } else {
                mPaint.color = Color.GRAY
            }

            canvas?.drawCircle(indexPaddingLeft + index * pointR, pointY, pointR * 0.2f, mPaint)
        }
    }


    fun clickBattery() {
        if (DEBUG) {
            Log.d("TAG", showIndex.toString())
        }
        if (showIndex < 0) {
            return
        }
        showIndex = (showIndex + 1) % Math.min(DATA_SIZE_MAX, mDateList.size)
        invalidate()
    }
}