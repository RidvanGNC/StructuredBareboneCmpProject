package org.structured_barebone.data.implementation

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.ServerException
import org.structured_barebone.shared.model.Result

abstract class BaseRepository {
    protected suspend inline fun <reified T> safeRequest(
        crossinline block: suspend () -> ApiResponse<T>
    ): Result<T> {
        return try {
            val response = block()
            if (response.success && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message ?: "Beklenmeyen bir hata oluştu.")
            }
        } catch (e: CancellationException) {
            // Leaving a screen cancels the request; that is not an error and must keep propagating.
            throw e
        } catch (e: ConnectTimeoutException) {
            Result.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        } catch (e: SocketTimeoutException) {
            Result.Error("Sunucu yanıt vermiyor. Lütfen tekrar deneyin.")
        } catch (e: IOException) {
            Result.Error("Bağlantı hatası. İnternet bağlantınızı kontrol edin.")
        } catch (e: ServerException) {
            Result.Error("Server Hatası: ${e.message}")
        } catch (e: Exception) {
            // The exception text is technical and must not reach the user.
            Result.Error("Beklenmeyen bir hata oluştu.")
        }
    }
}
