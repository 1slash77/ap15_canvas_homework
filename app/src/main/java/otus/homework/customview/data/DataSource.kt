package otus.homework.customview.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonArray
import otus.homework.customview.R

class DataSource(context: Context) {

    val data: List<Entity>
    val categories: List<String>
    val categoryDetails: List<CategorySummary>

    init {
        data = getData(context)
        categories = data.map { it.category?:"" }.toSet().toList()

        val totalAmount = data.sumOf { it.amount?:0 }
        categoryDetails = categories.map { catName ->
            val amount = data
                .filter { (it.category ?: "") == catName }
                .sumOf { it.amount?:0 }
            CategorySummary(
                name = catName,
                amount = amount,
                percentage = amount.toFloat() / totalAmount
            )
        }
    }

    private fun getData(ctx: Context): List<Entity> {
        val jsonStr = ctx.resources.openRawResource(R.raw.payload).bufferedReader().use { it.readText() }
        val gson = Gson()
        val jsonArr = gson.fromJson(jsonStr, JsonArray::class.java)
       return jsonArr.toList().map {
            gson.fromJson(it.toString(), Entity::class.java)
        }
    }

    fun getCategoryDetails(name: String): List<CategoryDetails> {
        return data
            .asSequence()
            .filter { it.category == name }
            .filter { it.time != null }
            .filter { it.amount != null }
            .sortedBy { it.time }
            .map { CategoryDetails(it.time!!.toLong(), it.amount!!) }
            .toList()
    }
}
