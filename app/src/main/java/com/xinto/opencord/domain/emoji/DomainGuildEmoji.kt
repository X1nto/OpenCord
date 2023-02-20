package com.xinto.opencord.domain.emoji

import androidx.compose.runtime.Immutable
import com.xinto.opencord.rest.service.DiscordCdnServiceImpl

@Immutable
data class DomainGuildEmoji(
    val id: Long,
    val name: String?,
    val animated: Boolean,
) : DomainEmoji {
    override val identifier: DomainEmojiIdentifier
        get() = id.hashCode() * animated.hashCode()

    val url: String
        get() = DiscordCdnServiceImpl.getGuildEmojiUrl(id, animated)

//    val isDeleted: Boolean
//        get() = name == null
}
