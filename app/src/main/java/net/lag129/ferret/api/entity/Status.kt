package net.lag129.ferret.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Status(
    @SerialName("id")
    val id: String,

    @SerialName("uri")
    val uri: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("account")
    val account: Account,

    @SerialName("content")
    val content: String,

    @SerialName("visibility")
    val visibility: String,

    @SerialName("sensitive")
    val sensitive: Boolean,

    @SerialName("spoiler_text")
    val spoilerText: String,

    // @SerialName("media_attachments")
    // val mediaAttachments: List<MediaAttachment>,

    @SerialName("application")
    val application: Application? = null,

    @SerialName("mentions")
    val mentions: List<Mention>,

    // @SerialName("tags")
    // val tags: List<Tag>,

    @SerialName("emojis")
    val emojis: List<CustomEmoji>,

    @SerialName("reblogs_count")
    val reblogsCount: Int,

    @SerialName("favourites_count")
    val favouritesCount: Int,

    @SerialName("replies_count")
    val repliesCount: Int,

    @SerialName("url")
    val url: String? = null,

    @SerialName("in_reply_to_id")
    val inReplyToId: String? = null,

    @SerialName("in_reply_to_account_id")
    val inReplyToAccountId: String? = null,

    @SerialName("reblog")
    val reblog: Status? = null,

    // @SerialName("poll")
    // val poll: Poll? = null,

    @SerialName("card")
    val card: PreviewCard? = null,

    @SerialName("language")
    val language: String? = null,

    @SerialName("text")
    val text: String? = null,

    @SerialName("edited_at")
    val editedAt: String? = null,

    @SerialName("favourited")
    val favourited: Boolean? = null,

    @SerialName("reblogged")
    val reblogged: Boolean? = null,

    @SerialName("muted")
    val muted: Boolean?,

    @SerialName("bookmarked")
    val bookmarked: Boolean?,

    @SerialName("pinned")
    val pinned: Boolean? = null,

    // @SerialName("filtered")
    // val filtered: List<FilterResult>?,

    @SerialName("emoji_reactions")
    val emojiReactions: List<Reaction>? = null,
)

@Serializable
data class Mention(
    @SerialName("id")
    val id: String,

    @SerialName("username")
    val username: String,

    @SerialName("url")
    val url: String,

    @SerialName("acct")
    val acct: String
)
