package com.hobsinnovations.hobsinn.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class LeaderboardEntry(
    val id: Long,
    val name: String,
    val ecoPoints: Long,
    val badges: List<String>
)

@RestController
@RequestMapping("/api/gamification")
class LeaderboardController(private val userRepository: UserRepository) {

    @GetMapping("/leaderboard")
    fun getLeaderboard(): List<LeaderboardEntry> {
        val topUsers = userRepository.findTop10ByOrderByEcoPointsDesc()
        return topUsers.map {
            LeaderboardEntry(
                id = it.id!!,
                name = it.name,
                ecoPoints = it.ecoPoints,
                badges = it.badges.toList()
            )
        }
    }
}
