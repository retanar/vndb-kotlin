package vlprojects.vndb.parameters

import com.google.gson.Gson

// default values are: page = 1, results = 10, sort = "id", reverse = false
data class Options(
    var results: Int? = null,
    var page: Int? = null,
    var sort: String? = null,
    var reverse: Boolean? = null,
) {
    override fun toString(): String =
        Gson().toJson(this)
}
