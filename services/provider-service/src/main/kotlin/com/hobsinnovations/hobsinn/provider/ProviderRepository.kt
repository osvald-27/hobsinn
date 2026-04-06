package com.hobsinnovations.hobsinn.provider

import org.springframework.data.jpa.repository.JpaRepository

interface ProviderRepository : JpaRepository<Provider, Long> {
    fun findByEmail(email: String): Provider?
    fun findByIsActive(isActive: Boolean): List<Provider>
}