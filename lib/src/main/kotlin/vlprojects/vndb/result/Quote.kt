package vlprojects.vndb.result

data class Quote(
    val id: Int,
    /** Title of the visual novel from which this quote was taken. */
    val title: String,
    /** Text of the quote. */
    val quote: String,
)
