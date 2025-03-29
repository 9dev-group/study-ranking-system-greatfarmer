package kr.co.music.service.impl

import kr.co.music.repository.ScoreboardRepository
import kr.co.music.service.ScoreboardService
import org.springframework.stereotype.Service

@Service
class ScoreboardServiceImpl(
    private val scoreboardRepository: ScoreboardRepository
): ScoreboardService {

}