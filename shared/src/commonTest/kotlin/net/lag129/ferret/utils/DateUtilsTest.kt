package net.lag129.ferret.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.datetime.TimeZone

class DateUtilsTest : StringSpec() {
    private val dateUtils = DateUtilsImpl { TimeZone.of("Asia/Tokyo") }

    init {
        "now" {
            dateUtils.getRelativeTime(
                CURRENT_TIME,
                CURRENT_TIME
            ) shouldBe RelativeTime.Now
        }

        "3 minutes ago" {
            dateUtils.getRelativeTime(
                CURRENT_TIME,
                CURRENT_TIME - 3.minutes
            ) shouldBe RelativeTime.Minutes(3)
        }

        "3 hours ago" {
            dateUtils.getRelativeTime(
                CURRENT_TIME,
                CURRENT_TIME - 3.hours
            ) shouldBe RelativeTime.Hours(3)
        }

        "3 days ago" {
            dateUtils.getRelativeTime(
                CURRENT_TIME,
                CURRENT_TIME - 3.days
            ) shouldBe RelativeTime.Date("2025/12/29")
        }
    }

    companion object {
        private val CURRENT_TIME = Instant.parse("2026-01-01T00:00:00+09:00")
    }
}
