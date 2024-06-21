package otus.homework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View

class ChartView @JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val list = ArrayList<Int>()
    private var maxValue = 0
    private var barWidth = 50.dp
    private val minH = 800.dp
    private lateinit var paintBaseFill : Paint
    private lateinit var paintStroke : Paint
    private val rect = RectF()

    init {
        if (isInEditMode) {
            setValues(listOf(4, 2, 1, 5, 0, 2))
        }

        paintBaseFill = Paint().apply {
            color = Color.parseColor("#ff00aaff")
            style = Paint.Style.FILL
        }

        paintStroke = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 2.0f
        }
    }

    fun setValues(values : List<Int>) {
        list.clear()
        list.addAll(values)
        maxValue = list.max()

        requestLayout()
        invalidate()
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

        var newW = wSize
        var newH = Integer.max(minH.toInt(), hSize)

        when (wMode) {
            MeasureSpec.EXACTLY -> {
            }
            MeasureSpec.AT_MOST -> {
                newW = Integer.min((list.size * barWidth).toInt(), wSize)
            }
            MeasureSpec.UNSPECIFIED -> newW = (list.size * barWidth).toInt()
        }

        setMeasuredDimension(newW, newH)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (list.size == 0) return

        val widthPerView = width.toFloat() / list.size
        var currentX = 0f
        val heightPerValue = height.toFloat() / maxValue

        for (item in list) {
            rect.set(
                currentX,
                (height - heightPerValue * item),
                (currentX + widthPerView),
                height.toFloat(),
            )
            canvas.drawRect(rect, paintBaseFill)
            canvas.drawRect(rect, paintStroke)
            currentX += widthPerView
        }
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putIntegerArrayList("values", list)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable?) {
        var state = state
        if (state is Bundle) // implicit null check
        {
            val bundle = state
            bundle.getIntegerArrayList("values")?.let {
                setValues(it.toList())
            }
            state = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }

}