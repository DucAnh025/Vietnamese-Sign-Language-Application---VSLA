package com.google.mediapipe.examples.gesturerecognizer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class PieChartView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var learnedPercentage: Float = 0f
    private var title: String = "Progress"

    fun setData(learned: Int, total: Int, chartTitle: String) {
        if (total > 0) {
            learnedPercentage = (learned / total.toFloat()) * 100
        }
        title = chartTitle
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = 50f
        val size = Math.min(width, height).toFloat()
        val chartBounds = RectF(padding, padding, size - padding, size - padding)

        // Vẽ phần đã học
        paint.color = Color.parseColor("#007F8B") // mp_color_primary
        val sweepAngle = (learnedPercentage / 100) * 360
        canvas.drawArc(chartBounds, -90f, sweepAngle, true, paint)

        // Vẽ phần chưa học
        paint.color = Color.LTGRAY
        canvas.drawArc(chartBounds, -90f + sweepAngle, 360 - sweepAngle, true, paint)

        // Vẽ tiêu đề
        paint.color = Color.BLACK
        paint.textSize = 40f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("$title: ${learnedPercentage.toInt()}%", width / 2f, height / 2f, paint)
    }
}
