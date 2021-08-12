package vlprojects.vndb.parameters

import com.google.gson.Gson

/**
 * Optional settings to use with get commands.
 *
 * Default server values are:
 * - results = 10
 * - page = 1
 * - sort = "id"
 * - reverse = false
 */
data class Options(
    var results: Int? = null,
    var page: Int? = null,
    /** Sort results by a field */
    var sort: String? = null,
    /** Reverse the order of results */
    var reverse: Boolean? = null,
) {
    override fun toString(): String =
        Gson().toJson(this)
}
