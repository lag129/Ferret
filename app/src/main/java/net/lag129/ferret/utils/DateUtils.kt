package net.lag129.ferret.utils

import android.content.Context
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import net.lag129.ferret.R
import kotlin.time.Instant

interface DateUtils {

    fun formatDateTime(
        time: Long,
    ): String

    fun getRelativeTimeSpanString(
        currentTime: Long,
        postedTime: Long
    ): String
}

class DateUtilsImpl(
    private val context: Context
) : DateUtils {

    override fun formatDateTime(time: Long): String {
        val dateTime =
            Instant.fromEpochMilliseconds(time).toLocalDateTime(TimeZone.currentSystemDefault())

        return "${dateTime.year}/${dateTime.month.number}/${dateTime.day} ${dateTime.hour}:${dateTime.minute}"
    }

    override fun getRelativeTimeSpanString(
        currentTime: Long,
        postedTime: Long
    ): String {

        val diffTime = currentTime - postedTime

        return when {
            diffTime < ONE_MINUTE_MILLIS -> context.getString(R.string.now)

            diffTime < ONE_HOUR_MILLIS -> context.getString(
                R.string.minute_abbreviation,
                (diffTime / ONE_MINUTE_MILLIS).toString()
            )

            diffTime < ONE_DAY_MILLIS -> context.getString(
                R.string.hour_abbreviation,
                (diffTime / ONE_HOUR_MILLIS).toString()
            )

            else -> Instant.fromEpochMilliseconds(postedTime)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let { dateTime ->
                    "${dateTime.year}/${dateTime.month.number}/${dateTime.day}"
                }
        }
    }

    companion object {
        private const val ONE_MINUTE_MILLIS = 60000L
        private const val ONE_HOUR_MILLIS = 3600000L
        private const val ONE_DAY_MILLIS = 86400000L
    }
}
