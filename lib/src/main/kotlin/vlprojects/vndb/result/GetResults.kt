package vlprojects.vndb.result

/**
 * Results that are returned from any get command. Actual data entries are contained in the [items].
 */
data class GetResults<T>(
    /** Number of returned results. */
    val num: Int,
    /** Indicates if there are more results (`true`), or no (`false`). */
    val more: Boolean,
    val items: List<T>,
)
