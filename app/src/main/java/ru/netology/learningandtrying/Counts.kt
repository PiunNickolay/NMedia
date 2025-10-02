package ru.netology.learningandtrying

object Counts {
    fun countFormat(count: Int): String {
        if (count >= 1_000_000) {
            val millions = count / 1_000_000
            val remainder = (count % 1_000_000) / 100_000
            return if (remainder > 0) {
                "$millions.${remainder}M"
            } else {
                "$millions M"
            }
        } else if (count >= 10_000) {
            val thousands = count / 1_000
            return "$thousands K"
        } else if (count >= 1_100) {
            val thousands = count / 1_000
            val remainder = (count % 1_000) / 100
            return if (remainder > 0) {
                "$thousands.${remainder}K"
            } else {
                "$thousands K"
            }
        } else if (count >= 1_000) {
            return "${count / 1_000}K"
        } else {
            return count.toString()
        }
    }
}
