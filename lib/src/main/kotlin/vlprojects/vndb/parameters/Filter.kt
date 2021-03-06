package vlprojects.vndb.parameters

/**
 * Filter to be used with some vndb commands. For detailed syntax explanation, see
 * [vndb api](https://vndb.org/d11#2) > *Filter string syntax*.
 */
open class Filter(
    private val filter: String
) {
    /** Wraps filter string with braces */
    override fun toString(): String {
        return "($filter)"
    }

    infix fun or(other: Filter) =
        Filter("$this or $other")

    infix fun and(other: Filter) =
        Filter("$this and $other")

    companion object {
        /** Empty filter that includes all results */
        val DEFAULT = Filter("id>=1")
    }
}

infix fun String.eq(value: Any) =
    Filter("$this=$value")

infix fun String.eq(value: String) =
    this eq "\"$value\"" as Any

infix fun String.eq(array: IntArray) =
    this eq array.joinToString(",", "[", "]") as Any

infix fun String.eq(array: Array<String>) =
    this eq array.joinToString(",", "[", "]") { item -> "\"$item\"" } as Any

infix fun String.not(value: Any) =
    Filter("$this!=$value")

infix fun String.not(value: String) =
    this not "\"$value\"" as Any

infix fun String.like(value: String) =
    Filter("$this~\"$value\"")