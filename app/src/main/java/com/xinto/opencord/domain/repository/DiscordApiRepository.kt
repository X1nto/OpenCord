package com.xinto.opencord.domain.repository

import com.xinto.opencord.domain.mapper.toApi
import com.xinto.opencord.domain.mapper.toDomain
import com.xinto.opencord.domain.model.*
import com.xinto.opencord.rest.body.MessageBody
import com.xinto.opencord.rest.service.DiscordApiService
import io.ktor.http.*

interface DiscordApiRepository {
    suspend fun getMeGuilds(): List<DomainMeGuild>
    suspend fun getGuild(guildId: ULong): DomainGuild
    suspend fun getGuildChannels(guildId: ULong): Map<ULong, DomainChannel>

    suspend fun getChannel(channelId: ULong): DomainChannel
    suspend fun getChannelMessages(channelId: ULong): Map<ULong, DomainMessage>
    suspend fun getChannelPins(channelId: ULong): Map<ULong, DomainMessage>

    suspend fun postChannelMessage(channelId: ULong, body: MessageBody)

    suspend fun getUserSettings(): DomainUserSettings
    suspend fun updateUserSettings(settings: DomainUserSettingsPartial): DomainUserSettings

    suspend fun addMeMessageReaction(
        channelId: ULong,
        messageId: ULong,
        emojiName: String? = null,
        emojiId: ULong? = null
    )
    suspend fun removeMeMessageReaction(
        channelId: ULong,
        messageId: ULong,
        emojiName: String? = null,
        emojiId: ULong? = null
    )
}

class DiscordApiRepositoryImpl(
    private val service: DiscordApiService
) : DiscordApiRepository {

    override suspend fun getMeGuilds(): List<DomainMeGuild> {
        return service.getMeGuilds().map { it.toDomain() }
    }

    override suspend fun getGuild(guildId: ULong): DomainGuild {
        return service.getGuild(guildId).toDomain()
    }

    override suspend fun getGuildChannels(guildId: ULong): Map<ULong, DomainChannel> {
        return service.getGuildChannels(guildId)
            .toList().associate {
                it.first.value to it.second.toDomain()
            }
    }

    override suspend fun getChannel(channelId: ULong): DomainChannel {
        return service.getChannel(channelId).toDomain()
    }

    override suspend fun getChannelMessages(channelId: ULong): Map<ULong, DomainMessage> {
        return service.getChannelMessages(channelId)
            .toList().associate {
                it.first.value to it.second.toDomain()
            }
    }

    override suspend fun getChannelPins(channelId: ULong): Map<ULong, DomainMessage> {
        return service.getChannelPins(channelId)
            .toList().associate {
                it.first.value to it.second.toDomain()
            }
    }

    override suspend fun postChannelMessage(channelId: ULong, body: MessageBody) {
        service.postChannelMessage(channelId, body)
    }

    override suspend fun getUserSettings(): DomainUserSettings {
        return service.getUserSettings().toDomain()
    }

    override suspend fun updateUserSettings(settings: DomainUserSettingsPartial): DomainUserSettings {
        return service.updateUserSettings(settings.toApi()).toDomain()
    }

    override suspend fun addMeMessageReaction(
        channelId: ULong,
        messageId: ULong,
        emojiName: String?,
        emojiId: ULong?
    ) {
        val encodedEmote = getEncodedEmote(emojiName, emojiId)
        service.createMeMessageReaction(
            channelId = channelId,
            messageId = messageId,
            encodedEmote = encodedEmote
        )
    }

    override suspend fun removeMeMessageReaction(
        channelId: ULong,
        messageId: ULong,
        emojiName: String?,
        emojiId: ULong?
    ) {
        val encodedEmote = getEncodedEmote(emojiName, emojiId)
        service.removeMeMessageReaction(
            channelId = channelId,
            messageId = messageId,
            encodedEmote = encodedEmote
        )
    }

    private fun getEncodedEmote(emojiName: String?, emojiId: ULong?): String {
        return if (emojiId != null) {
            "$emojiName:$emojiId"
        } else {
            emojiName
        }!!.encodeURLParameter()
    }
}
