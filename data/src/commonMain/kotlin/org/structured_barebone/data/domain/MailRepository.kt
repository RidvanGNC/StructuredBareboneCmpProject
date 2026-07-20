package org.structured_barebone.data.domain

import org.structured_barebone.shared.model.CustomerDto
import org.structured_barebone.shared.model.MailDto
import org.structured_barebone.shared.model.MailStatus
import org.structured_barebone.shared.model.MailType
import org.structured_barebone.shared.model.Result

data class MailFilter(
    val status: MailStatus? = null,
    val predictedLanguage: String? = null,
    val confirmedLanguage: String? = null,
    val type: MailType? = null,
    val hasAttachment: Boolean? = null,
    val isUrgent: Boolean? = null,
    val mailGroup: String? = null,
    val replyStatus: String? = null,
    val isNotarized: Boolean? = null,
)

interface MailRepository {
    suspend fun getMails(filter: MailFilter = MailFilter()): Result<List<MailDto>>
    suspend fun getMailById(id: String): Result<MailDto>
    suspend fun getCustomers(): Result<List<CustomerDto>>
    suspend fun getMailGroups(): Result<List<String>>
    suspend fun syncMails(): Result<Int>
    suspend fun confirmLanguage(id: String, language: String): Result<Unit>
    suspend fun reanalyze(id: String): Result<MailDto>
    suspend fun downloadAttachment(id: String): Result<ByteArray>
}
