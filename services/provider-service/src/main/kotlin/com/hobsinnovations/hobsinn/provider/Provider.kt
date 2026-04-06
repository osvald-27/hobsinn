package com.hobsinnovations.hobsinn.provider

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "providers")
data class Provider(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    val phone: String,

    @Column(nullable = false)
    val address: String,

    @Column(nullable = false)
    val isActive: Boolean = true,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)