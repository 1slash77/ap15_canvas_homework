package otus.homework.customview



import android.animation.ValueAnimator
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.BounceInterpolator
import androidx.appcompat.app.AppCompatActivity
import otus.homework.customview.data.DataSource


class MainActivity : AppCompatActivity() {

    private val TAG = "mytag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val pieChart = findViewById<PieChartView>(R.id.pie_chart)
        val chart = findViewById<ChartView>(R.id.chart)

        val data = DataSource(this);
        pieChart.setData(data.categoryDetails)
        pieChart.setOnItemSelected {
            if (it >= 0) {
                val cat = data.categories[it]
                val values = data.getCategoryDetails(cat).map { it.amount }
                Log.d(TAG, "$cat: $values")
                chart.scaleX = 0f
                chart.scaleY = 0f
                chart.animate().scaleX(1f).scaleY(1f).start()
                chart.setValues(values)
                chart.visibility = View.VISIBLE
            } else {
                chart.animate().scaleX(0f).scaleY(0f).start()
                chart.visibility = View.GONE
            }

        }

        Log.d(TAG, "data: " + data.categories);
    }
}

val Int.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Int.sp: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity)