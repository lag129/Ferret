package net.lag129.ferret.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.lag129.ferret.model.mastodon.Attachment
import net.lag129.ferret.model.mastodon.PreviewCard
import net.lag129.ferret.model.mastodon.Reaction

@Immutable
@Serializable
data class Post(
    val id: String,
    val displayName: String,
    val userName: String,
    val avatarUrl: String,
    val createdAt: String,
    val content: String,
    val profile: Profile,
    val card: PreviewCard? = null,
    val displayNameEmojis: List<Emoji> = emptyList(),
    val emojis: List<Emoji> = emptyList(),
    val mediaAttachments: List<Attachment> = emptyList(),
    val sensitive: Boolean,
    val spoilerText: String,
    val reactions: List<Reaction> = emptyList()
) {
    @Immutable
    @Serializable
    data class Emoji(
        @SerialName("shortcode")
        val shortcode: String,

        @SerialName("url")
        val url: String,

        @SerialName("width")
        val width: Int? = null,

        @SerialName("height")
        val height: Int? = null,
    )
}
