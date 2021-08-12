package vlprojects.vndb.result

/**
 * Structured response from the vndb server.
 */
data class Response(
    val name: String,
    val json: String = "",
)
