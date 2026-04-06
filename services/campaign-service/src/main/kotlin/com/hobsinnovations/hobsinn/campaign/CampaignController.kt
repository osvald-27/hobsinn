package com.hobsinnovations.hobsinn.campaign

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/campaigns")
class CampaignController(
    private val campaignService: CampaignService
) {

    @PostMapping
    fun createCampaign(@RequestBody request: CreateCampaignRequest): ResponseEntity<Campaign> {
        val campaign = campaignService.createCampaign(request)
        return ResponseEntity.ok(campaign)
    }

    @GetMapping("/{id}")
    fun getCampaign(@PathVariable id: Long): ResponseEntity<Campaign> {
        val campaign = campaignService.getCampaignById(id)
        return if (campaign != null) ResponseEntity.ok(campaign) else ResponseEntity.notFound().build()
    }

    @GetMapping
    fun getAllCampaigns(): ResponseEntity<List<Campaign>> {
        val campaigns = campaignService.getAllCampaigns()
        return ResponseEntity.ok(campaigns)
    }

    @PutMapping("/{id}")
    fun updateCampaign(@PathVariable id: Long, @RequestBody request: UpdateCampaignRequest): ResponseEntity<Campaign> {
        val campaign = campaignService.updateCampaign(id, request)
        return if (campaign != null) ResponseEntity.ok(campaign) else ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deleteCampaign(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = campaignService.deleteCampaign(id)
        return if (deleted) ResponseEntity.noContent().build() else ResponseEntity.notFound().build()
    }
}