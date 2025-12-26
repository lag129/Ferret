package net.lag129.ferret.api.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    @SerialName("id")
    val id: String,

    @SerialName("username")
    val username: String,

    @SerialName("acct")
    val acct: String,

    @SerialName("url")
    val url: String,

    @SerialName("display_name")
    val displayName: String,

    @SerialName("note")
    val note: String,

    @SerialName("avatar")
    val avatar: String,

    @SerialName("avatar_static")
    val avatarStatic: String,

    @SerialName("header")
    val header: String,

    @SerialName("header_static")
    val headerStatic: String,

    @SerialName("locked")
    val locked: Boolean,

    @SerialName("fields")
    val fields: List<Field>,

    @SerialName("emojis")
    val emojis: List<CustomEmoji>,

    @SerialName("bot")
    val bot: Boolean,

    @SerialName("group")
    val group: Boolean,

    @SerialName("discoverable")
    val discoverable: Boolean? = null,

    @SerialName("noindex")
    val noindex: Boolean? = null,

    @SerialName("moved")
    val moved: Account? = null,

    @SerialName("suspended")
    val suspended: Boolean? = null,

    @SerialName("limited")
    val limited: Boolean? = null,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("last_status_at")
    val lastStatusAt: String? = null,

    @SerialName("statuses_count")
    val statusesCount: Int,

    @SerialName("followers_count")
    val followersCount: Int,

    @SerialName("following_count")
    val followingCount: Int,
)

@Serializable
data class Field(
    @SerialName("name")
    val name: String,

    @SerialName("value")
    val value: String,

    @SerialName("verified_at")
    val verifiedAt: String? = null
)
