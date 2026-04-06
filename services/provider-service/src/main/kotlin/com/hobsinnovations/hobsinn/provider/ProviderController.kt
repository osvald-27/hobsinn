package com.hobsinnovations.hobsinn.provider

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/providers")
class ProviderController(
    private val providerService: ProviderService
) {

    @PostMapping
    fun createProvider(@RequestBody request: CreateProviderRequest): ResponseEntity<Provider> {
        val provider = providerService.createProvider(request)
        return ResponseEntity.ok(provider)
    }

    @GetMapping("/{id}")
    fun getProvider(@PathVariable id: Long): ResponseEntity<Provider> {
        val provider = providerService.getProviderById(id)
        return if (provider != null) ResponseEntity.ok(provider) else ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAllProviders(): ResponseEntity<List<Provider>> {
        val providers = providerService.getAllProviders()
        return ResponseEntity.ok(providers)
    }

    @GetMapping("/active")
    fun getActiveProviders(): ResponseEntity<List<Provider>> {
        val providers = providerService.getActiveProviders()
        return ResponseEntity.ok(providers)
    }

    @PutMapping("/{id}")
    fun updateProvider(@PathVariable id: Long, @RequestBody request: UpdateProviderRequest): ResponseEntity<Provider> {
        val provider = providerService.updateProvider(id, request)
        return if (provider != null) ResponseEntity.ok(provider) else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteProvider(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = providerService.deleteProvider(id)
        return if (deleted) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }
}