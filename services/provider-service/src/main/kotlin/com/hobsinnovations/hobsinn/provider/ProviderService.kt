package com.hobsinnovations.hobsinn.provider

import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProviderService(
    private val providerRepository: ProviderRepository
) {

    fun createProvider(request: CreateProviderRequest): Provider {
        if (providerRepository.findByEmail(request.email) != null) {
            throw IllegalArgumentException("Email already exists")
        }

        val provider = Provider(
            name = request.name,
            email = request.email,
            phone = request.phone,
            address = request.address,
            isActive = request.isActive
        )
        return providerRepository.save(provider)
    }

    fun getProviderById(id: Long): Provider? {
        return providerRepository.findById(id).orElse(null)
    }

    fun getAllProviders(): List<Provider> {
        return providerRepository.findAll()
    }

    fun getActiveProviders(): List<Provider> {
        return providerRepository.findByIsActive(true)
    }

    fun updateProvider(id: Long, request: UpdateProviderRequest): Provider? {
        val provider = providerRepository.findById(id).orElse(null) ?: return null

        val updatedProvider = provider.copy(
            name = request.name ?: provider.name,
            phone = request.phone ?: provider.phone,
            address = request.address ?: provider.address,
            isActive = request.isActive ?: provider.isActive,
            updatedAt = LocalDateTime.now()
        )
        return providerRepository.save(updatedProvider)
    }

    fun deleteProvider(id: Long): Boolean {
        if (!providerRepository.existsById(id)) return false
        providerRepository.deleteById(id)
        return true
    }
}

data class CreateProviderRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val isActive: Boolean = true
)

data class UpdateProviderRequest(
    val name: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val isActive: Boolean? = null
)