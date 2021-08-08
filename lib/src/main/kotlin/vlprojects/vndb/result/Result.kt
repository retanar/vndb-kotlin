package vlprojects.vndb.result

sealed class Result<out T> {
    class Error(val json: String) : Result<Nothing>()
    class Success<out T>(val data: T) : Result<T>()
//    class Ok : Success<Unit>(Unit)
}
