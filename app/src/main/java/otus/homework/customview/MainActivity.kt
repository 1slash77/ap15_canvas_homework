package otus.homework.customview



import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
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
                chart.visibility = View.VISIBLE
                chart.setValues(values)
            } else {
                chart.visibility = View.GONE
            }

        }

        Log.d(TAG, "data: " + data.categories);

/*        data.categories.forEach {
            Log.d(TAG, "     $it")
            val cd = data.getCategoryDetails(it)
            val cal = Calendar.getInstance()
            cd.forEach {
                cal.timeInMillis = it.time * 1000
                Log.d(TAG, "${it.time} - ${cal.time}")
            }
        }*/
    }
}

val Int.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Int.sp: Float
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity)