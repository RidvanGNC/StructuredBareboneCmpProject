package org.structured_barebone.data.implementation

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import org.structured_barebone.data.client.AppLog
import org.structured_barebone.data.client.toAppError
import org.structured_barebone.data.model.ServerException
import org.structured_barebone.shared.model.AppError
import org.structured_barebone.shared.model.ErrorKind
import org.structured_barebone.shared.model.Result

abstract class BaseRepository {
    /**
     * Runs a network call and turns every known failure into [Result.Error]. Failures carry data only
     * (kind, code, trace id); the UI chooses the localized text. Cancellation is never an error.
     */
    protected suspend inline fun <T> safeCall(crossinline block: suspend () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: CancellationException) {
            // Leaving a screen cancels the request; that is not an error and must keep propagating.
            throw e
        } catch (e: ServerException) {
            AppLog.d("Request failed: ${e.message}")
            Result.Error(e.toAppError())
        } catch (e: ConnectTimeoutException) {
            AppLog.d("Request failed: ${e::class.simpleName}")
            Result.Error(AppError(ErrorKind.NETWORK_UNREACHABLE))
        } catch (e: SocketTimeoutException) {
            AppLog.d("Request failed: ${e::class.simpleName}")
            Result.Error(AppError(ErrorKind.TIMEOUT))
        } catch (e: HttpRequestTimeoutException) {
            AppLog.d("Request failed: ${e::class.simpleName}")
            Result.Error(AppError(ErrorKind.TIMEOUT))
        } catch (e: IOException) {
            AppLog.d("Request failed: ${e::class.simpleName}")
            Result.Error(AppError(ErrorKind.NETWORK_UNREACHABLE))
        } catch (e: SerializationException) {
            AppLog.d("Response could not be read: ${e::class.simpleName}")
            Result.Error(AppError(ErrorKind.UNEXPECTED, detail = e::class.simpleName))
        } catch (e: Exception) {
            // The exception text is technical and must not reach the user.
            AppLog.d("Unexpected failure: ${e::class.simpleName}")
            Result.Error(AppError(ErrorKind.UNEXPECTED, detail = e::class.simpleName))
        }
    }
}
