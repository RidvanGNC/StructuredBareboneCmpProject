package org.structured_barebone.data.implementation

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.io.IOException
import org.structured_barebone.data.model.ApiResponse
import org.structured_barebone.data.model.ServerException
import org.structured_barebone.shared.model.Result

abstract class BaseRepository {
    protected inline fun <reified T> safeRequest(
        block: () -> ApiResponse<T>
    ): Result<T> {
        return try {
            val response = block()
            if (response.success && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message ?: "Beklenmeyen bir hata oluştu.")
            }
        } catch (e: ConnectTimeoutException) {
            Result.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        } catch (e: SocketTimeoutException) {
            Result.Error("Sunucu yanıt vermiyor. Lütfen tekrar deneyin.")
        } catch (e: IOException) {
            Result.Error("Bağlantı hatası. İnternet bağlantınızı kontrol edin.")
        } catch (e: ServerException) {
            Result.Error("Server Hatası: ${e.message}")
        } catch (e: Exception) {
            Result.Error("Bir hata oluştu: ${e.message}")
        }
    }
}