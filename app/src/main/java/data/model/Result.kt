package data.model

enum class Status { SUCCESS, ERROR, LOADING }

data class Result<M, E : Throwable>(val status: Status, val response: M?, val errorModel: E?, val message: String?) {
    companion object {
        fun <M, E : Throwable> success(response: M?): Result<M, E> {
            return Result(Status.SUCCESS, response, null, null)
        }

        fun <M, E : Throwable> error(msg: String,errorModel: E): Result<M, E> {
            return Result(Status.ERROR, null, errorModel, msg)
        }

        fun <M, E : Throwable> loading(): Result<M, E> {
            return Result(Status.LOADING, null, null, null)
        }
    }

}

class BaseErrorModel : Throwable() {
    var errorType: Int = 0
    var errorTitle: String? = null
    var errorMessage: String? = null
    var serverErrorCode: Int? = null
    var serverErrorText: String? = null
    var serverResponse: String? = null
}