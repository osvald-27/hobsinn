package com.hobsinnovations.hobsinn.campaign

import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CampaignService(
    private val campaignRepository: CampaignRepository
) {

    fun createCampaign(request: CreateCampaignRequest): Campaign {
        val campaign = Campaign(
            name = request.name,
            description = request.description,
            startDate = request.startDate,
            endDate = request.endDate,
            isActive = request.isActive
        )
        return campaignRepository.save(campaign)
    }

    fun getCampaignById(id: Long): Campaign? {
        return campaignRepository.findById(id).orElse(null)
    }

    fun getAllCampaigns(): List<Campaign> {
        return campaignRepository.findAll()
    }

    fun updateCampaign(id: Long, request: UpdateCampaignRequest): Campaign? {
        val campaign = campaignRepository.findById(id).orElse(null) ?: return null

        val updatedCampaign = campaign.copy(
            name = request.name ?: campaign.name,
            description = request.description ?: campaign.description,
            startDate = request.startDate ?: campaign.startDate,
            endDate = request.endDate ?: campaign.endDate,
            isActive = request.isActive ?: campaign.isActive,
            updatedAt = LocalDateTime.now()
        )
        return campaignRepository.save(updatedCampaign)
    }

    fun deleteCampaign(id: Long): Boolean {
        if (!campaignRepository.existsById(id)) return false
        campaignRepository.deleteById(id)
        return true
    }
}

data class CreateCampaignRequest(
    val name: String,
    val description: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val isActive: Boolean = true
)

data class UpdateCampaignRequest(
    val name: String? = null,
    val description: String? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val isActive: Boolean? = null
)