package com.hobsinnovations.hobsinn.domain

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Immutable monetary amount in XAF (CFA Francs).
 * Uses BigDecimal — never Double or Float for money.
 *
 * Platform fee formula (per spec):
 *   platformFee = 100 XAF + (5% of estimated cost IF estimated cost > 1100 XAF)
 *   totalShownToUser = estimatedCost + platformFee
 */
data class MoneyAmount(val amountXaf: BigDecimal) {
    init {
        require(amountXaf >= BigDecimal.ZERO) { "Amount cannot be negative: $amountXaf" }
    }

    constructor(v: Long)   : this(BigDecimal.valueOf(v))
    constructor(v: Int)    : this(BigDecimal.valueOf(v.toLong()))
    constructor(v: Double) : this(BigDecimal.valueOf(v))

    operator fun plus(other: MoneyAmount)  = MoneyAmount(amountXaf + other.amountXaf)
    operator fun minus(other: MoneyAmount) = MoneyAmount(amountXaf - other.amountXaf)
    operator fun times(factor: Double)     = MoneyAmount(amountXaf.multiply(BigDecimal.valueOf(factor)))
    operator fun compareTo(other: MoneyAmount) = amountXaf.compareTo(other.amountXaf)

    fun toLong(): Long = amountXaf.setScale(0, RoundingMode.HALF_UP).toLong()

    override fun toString(): String = "${toLong()} XAF"

    companion object {
        val ZERO = MoneyAmount(BigDecimal.ZERO)

        /**
         * Calculates the platform fee per the hobsinn spec:
         * 100 XAF base + 5% of estimated cost if estimated cost > 1100 XAF.
         */
        fun platformFee(estimatedCost: MoneyAmount): MoneyAmount {
            val base = MoneyAmount(100L)
            val threshold = MoneyAmount(1100L)
            return if (estimatedCost > threshold) {
                base + (estimatedCost * 0.05)
            } else {
                base
            }
        }

        /**
         * Total shown to the user = estimated cost + platform fee.
         * The fee breakdown is NEVER shown to the user.
         */
        fun totalForUser(estimatedCost: MoneyAmount): MoneyAmount =
            estimatedCost + platformFee(estimatedCost)
    }
}
