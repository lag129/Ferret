package net.lag129.ferret.ui.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.lag129.ferret.model.Account
import net.lag129.ferret.model.Attachment
import net.lag129.ferret.model.PreviewCard
import net.lag129.ferret.model.Reaction

@Immutable
@Serializable
data class Post(
    val id: String,
    val displayName: String,
    val userName: String,
    val avatarUrl: String,
    val createdAt: String,
    val content: String,
    val account: Account? = null,
    val card: PreviewCard? = null,
    val displayNameEmojis: List<Emoji>? = null,
    val emojis: List<Emoji>? = null,
    val mediaAttachments: List<Attachment>? = null,
    val sensitive: Boolean,
    val spoilerText: String,
    val reactions: List<Reaction> = emptyList()
) {
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
