package vlprojects.vndb.result

data class GetResults<T>(
    val num: Int,
    val more: Boolean,
    val items: List<T>,
)
