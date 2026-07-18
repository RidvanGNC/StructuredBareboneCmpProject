package org.structured_barebone.shared.theme

import androidx.compose.ui.graphics.Color
import org.structured_barebone.shared.model.MailStatus
import org.structured_barebone.shared.model.MailType

// Web'deki mailTypeStyles.ts ile birebir eslesen renkler; ekran modulleri arasinda paylasilir.
object MailTypeColors {
    val Advertisement = Color(0xFFFAAD14)
    val Spam = Color(0xFFFF4D4F)
    val InquiryOrOffer = Color(0xFF1677FF)
    val Translation = Color(0xFF52C41A)

    fun forType(type: MailType): Color = when (type) {
        MailType.Advertisement -> Advertisement
        MailType.Spam -> Spam
        MailType.InquiryOrOffer -> InquiryOrOffer
        MailType.Translation -> Translation
    }
}

object MailStatusColors {
    fun forStatus(status: MailStatus): Color = when (status) {
        MailStatus.NotCompleted -> Color(0xFFFAAD14)
        MailStatus.InProgress -> Color(0xFF1677FF)
        MailStatus.Completed -> Color(0xFF52C41A)
    }
}
