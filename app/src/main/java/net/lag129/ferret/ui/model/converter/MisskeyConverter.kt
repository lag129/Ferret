package net.lag129.ferret.ui.model.converter

import net.lag129.ferret.model.misskey.Note
import net.lag129.ferret.model.misskey.UserLite
import net.lag129.ferret.ui.model.Post
import net.lag129.ferret.ui.model.Profile

object MisskeyConverter {
    fun Note.toPost(): Post {
        val target = if (text == null && renote != null) renote else this
        return Post(
            id = id,
            displayName = target.user.name ?: target.user.username,
            userName = target.user.host?.let { "${target.user.username}@$it" } ?: target.user.username,
            avatarUrl = target.user.avatarUrl,
            createdAt = target.createdAt,
            content = target.text.orEmpty(),
            profile = target.user.toProfile(),
            displayNameEmojis = target.user.emojis.map { (shortcode, url) ->
                Post.Emoji(
                    shortcode = shortcode,
                    url = url
                )
            },
            sensitive = target.cw != null,
            spoilerText = target.cw.orEmpty(),
        )
    }

    fun UserLite.toProfile(): Profile {
        return Profile(
            id = id,
            username = username,
            acct = host?.let { "$username@$it" } ?: username,
            url = "",
            displayName = name ?: username,
            note = "",
            avatar = avatarUrl,
            header = "",
            emojis = emojis.map { (shortcode, url) ->
                Post.Emoji(
                    shortcode = shortcode,
                    url = url
                )
            },
            statusesCount = 0,
            followersCount = 0,
            followingCount = 0,
        )
    }
}
