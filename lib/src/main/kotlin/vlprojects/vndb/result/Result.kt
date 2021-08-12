package vlprojects.vndb.result

sealed class Result<out T> {
    /**
     * Error result containing a single json. Json object always contains an "id" property to identify the error and a
     * "msg" property that contains human readable message explaining what went wrong. Other properties are also
     * possible, depending on the value of "id". For a full list of ids and additional properties please refer to
     * [vndb api](https://vndb.org/d11#7).
     */
    class Error(val json: String) : Result<Nothing>()

    /** Successful result that holds object [T]. */
    class Success<out T>(val data: T) : Result<T>()
}
