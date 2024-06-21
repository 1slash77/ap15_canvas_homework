package otus.homework.customview.data

import com.google.gson.annotations.SerializedName

data class Entity(
        @SerializedName("id"       ) val id       : Int?    = null,
        @SerializedName("name"     ) val name     : String? = null,
        @SerializedName("amount"   ) val amount   : Int?    = null,
        @SerializedName("category" ) val category : String? = null,
        @SerializedName("time"     ) val time     : Int?    = null
    )