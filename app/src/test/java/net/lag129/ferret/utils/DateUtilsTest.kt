package net.lag129.ferret.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DateUtilsTest : StringSpec() {
    val dateUtils = DateUtilsImpl()

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
                CURRENT_TIME - 3L * ONE_MINUTE_MILLIS
            ) shouldBe RelativeTime.Minutes(3)
        }

        "3 hours ago" {
            dateUtils.getRelativeTime(
                CURRENT_TIME,
                CURRENT_TIME - 3L * ONE_HOUR_MILLIS
            ) shouldBe RelativeTime.Hours(3)
        }

        "3 days ago" {
            dateUtils.getRelativeTime(
                CURRENT_TIME,
                CURRENT_TIME - 3L * ONE_DAY_MILLIS
            ) shouldBe RelativeTime.Date("2025/12/29")
        }
    }

    companion object {
        /**
         * 2026/01/01 00:00:00
         */
        private const val CURRENT_TIME = 1767193200000L
        private const val ONE_MINUTE_MILLIS = 60000L
        private const val ONE_HOUR_MILLIS = 3600000L
        private const val ONE_DAY_MILLIS = 86400000L
    }
}
