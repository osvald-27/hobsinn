package com.hobsinnovations.hobsinn.payment

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService
) {

    @PostMapping
    fun createPayment(@RequestBody request: CreatePaymentRequest): ResponseEntity<Payment> {
        val payment = paymentService.createPayment(request)
        return ResponseEntity.ok(payment)
    }

    @GetMapping("/{id}")
    fun getPayment(@PathVariable id: Long): ResponseEntity<Payment> {
        val payment = paymentService.getPaymentById(id)
        return if (payment != null) ResponseEntity.ok(payment) else ResponseEntity.notFound().build()
    }

    @GetMapping("/user/{userId}")
    fun getPaymentsByUser(@PathVariable userId: Long): ResponseEntity<List<Payment>> {
        val payments = paymentService.getPaymentsByUserId(userId)
        return ResponseEntity.ok(payments)
    }

    @PutMapping("/{id}/status")
    fun updatePaymentStatus(@PathVariable id: Long, @RequestParam status: PaymentStatus): ResponseEntity<Payment> {
        val payment = paymentService.updatePaymentStatus(id, status)
        return if (payment != null) ResponseEntity.ok(payment) else ResponseEntity.notFound().build()
    }
}