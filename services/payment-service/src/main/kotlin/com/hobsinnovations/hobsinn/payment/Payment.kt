package com.hobsinnovations.hobsinn.payment

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "payments")
data class Payment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false)
    val currency: String = "XAF",

    @Enumerated(EnumType.STRING)
    val status: PaymentStatus = PaymentStatus.PENDING,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, CANCELLED
}