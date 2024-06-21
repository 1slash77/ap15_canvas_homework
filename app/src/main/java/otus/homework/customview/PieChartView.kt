package otus.homework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import otus.homework.customview.data.CategorySummary
import kotlin.math.atan
import kotlin.math.min
import kotlin.math.sqrt


class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val sb = StringBuilder()

    private data class Category(
        val name: String,
        val startAngle: Float,
        val angle: Float,
        val color: Color
    )
    private var model: List<Category> = emptyList()

    private var selectedIndex = -1;

    private var onItemSelected: ((Int) -> Unit)? = null

    private val wrapContentWidthDp = 200
    private val wrapContentWidthPx: Float

    private val colors = listOf(
        Color.Red,
        Color.Blue,
        Color.Cyan,
        Color.Gray,
        Color.Yellow,
        Color.Green,
        Color.Magenta,
        Color.LightGray,
        Color.DarkGray,
        Color.Black,
    )

    private val paintBorder = Paint().apply {
        strokeWidth = 5f
        style = Paint.Style.STROKE
        color = Color.Black.toArgb()
    }

    private val paintFill = Paint().apply {
        style = Paint.Style.FILL
    }

    private val rect = RectF();

    init {
        wrapContentWidthPx = resources.displayMetrics.density * wrapContentWidthDp
    }

    fun setData(input: List<CategorySummary>) {
        paintFill.colorFilter
        val _data = mutableListOf<Category>()
        var startAngle = -90f
        var angle: Float
        var color: Color

        val angleForDivider = 0f;
        val angleRemain = 360f - angleForDivider * input.size;

        input.forEachIndexed { index, cat ->
            color = colors.getOrNull(index) ?: Color.Black
            angle = cat.percentage * angleRemain
            _data.add(
                Category(
                    name = cat.name,
                    startAngle = startAngle,
                    angle = angle,
                    color = color
                )
            )
            startAngle += angle + angleForDivider
        }
        model = _data.toList()

        requestLayout()
        invalidate()
    }

    fun setOnItemSelected(l: ((Int) -> Unit)?) {
        onItemSelected = l
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

       sb.clear().append("${this.hashCode()}, ")

        var w: Int = 0
        when(wMode) {
            MeasureSpec.EXACTLY -> {
                sb.append("w-EXACTLY")
                w = wSize
            }
            MeasureSpec.AT_MOST -> {
                sb.append("w-AT_MOST")
                w = min(wrapContentWidthPx, wSize.toFloat()).toInt()
            }
            MeasureSpec.UNSPECIFIED -> {
                sb.append("w-UNSPECIFIED")
                w = wrapContentWidthPx.toInt()
            }
        }

        sb.append(", ")

        var h: Int = 0
        when(hMode) {
            MeasureSpec.EXACTLY -> {
                sb.append("h-EXACTLY")
                h = hSize
            }
            MeasureSpec.AT_MOST -> {
                sb.append("h-AT_MOST")
                h = min(wrapContentWidthPx, hSize.toFloat()).toInt()
            }
            MeasureSpec.UNSPECIFIED -> {
                sb.append("h-UNSPECIFIED")
                h = wrapContentWidthPx.toInt()
            }
        }

        sb.append(", ${wSize}x${hSize}")
        val minWH = min(w, h)
        sb.append(", ${w}x${h} -> $minWH")

        Log.d("mytag", sb.toString())
        setMeasuredDimension(minWH, minWH)
    }


    private var cx = 0f
    private var cy = 0f
    private var r = 0f
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cx = width.toFloat() / 2
        cy = height.toFloat() / 2
        r = min(width.toFloat(), height.toFloat()) / 2

        Log.d("mytag", "$cx,$cy r=$r")

        rect.set(cx - r, cy - r, cx + r, cy + r);

        model.forEachIndexed {index, cat ->
            //Log.d("mytag", "${cat.name}, ${cat.amount}, ${cat.percentage}, ${angle}")
            paintFill.color = cat.color.toArgb()
            canvas.drawArc(rect, cat.startAngle, cat.angle, true, paintFill)
            if (index == selectedIndex) {
                canvas.drawArc(rect, cat.startAngle, cat.angle, true, paintBorder)
            }
        }
    }

    private val generalGestureDetector = GestureDetector(context, object: SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            Log.d("mytag", "onSingleTapConfirmed");
            val x = e.x - cx
            val y = e.y - cy
            if (sqrt((x * x + y * y.toDouble())) <= r) {
                var angle = Math.toDegrees(atan(y / x).toDouble())
                if (x < 0) angle += 180;
                val index = getCatIndexByAngle(angle)
                if (index >= 0) {
                    if (index == selectedIndex) {
                        selectedIndex = -1;
                    } else {
                        selectedIndex = index;
                    }
                    invalidate();
                    onItemSelected?.invoke(selectedIndex)
                }
                return true
            }
            return super.onSingleTapConfirmed(e)
        }
    })

    private fun getCatIndexByAngle(angle: Double): Int {
        model.firstOrNull { (angle >= it.startAngle && angle < (it.startAngle + it.angle))}?.let {
            return model.indexOf(it);
        }
        return -1;
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        generalGestureDetector.onTouchEvent(event)
        return true
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("index", selectedIndex)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable?) {
        var state = state
        if (state is Bundle)
        {
            val bundle = state
            selectedIndex = bundle.getInt("index")
            onItemSelected?.invoke(selectedIndex)
            state = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }
}