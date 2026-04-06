package com.hobsinnovations.hobsinn.payment

import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository
) {

    fun createPayment(request: CreatePaymentRequest): Payment {
        val payment = Payment(
            userId = request.userId,
            amount = request.amount,
            currency = request.currency,
            status = PaymentStatus.PENDING
        )
        return paymentRepository.save(payment)
    }

    fun getPaymentById(id: Long): Payment? {
        return paymentRepository.findById(id).orElse(null)
    }

    fun getPaymentsByUserId(userId: Long): List<Payment> {
        return paymentRepository.findAll().filter { it.userId == userId }
    }

    fun updatePaymentStatus(id: Long, status: PaymentStatus): Payment? {
        val payment = paymentRepository.findById(id).orElse(null) ?: return null

        val updatedPayment = payment.copy(
            status = status,
            updatedAt = LocalDateTime.now()
        )
        return paymentRepository.save(updatedPayment)
    }
}

data class CreatePaymentRequest(
    val userId: Long,
    val amount: BigDecimal,
    val currency: String = "XAF"
)