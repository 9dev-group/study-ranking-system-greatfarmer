package kr.co.music.service.impl

import kr.co.music.domain.MusicEntity
import kr.co.music.dto.MusicScoreEventDto
import kr.co.music.dto.ScoreboardResponseDto
import kr.co.music.repository.MusicRepository
import kr.co.music.repository.ScoreboardRepository
import kr.co.music.service.MusicService
import kr.co.music.util.Log
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageRequest
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MusicServiceImpl(
    private val musicRepository: MusicRepository,
    private val scoreboardRepository: ScoreboardRepository,
    private val eventPublisher: ApplicationEventPublisher
): MusicService, Log {

    @Retryable(maxAttempts = 1)
    @Cacheable(cacheNames = ["musicCache"], key = "'musicId::' + #id")
    override fun getMusicById(id: Int): ScoreboardResponseDto? {
        return findMusicScoreByMusicId(id)
    }

    @Recover
    fun recoverGetMusicById(e: Exception, id: Int): ScoreboardResponseDto? {
        logger.error(e.message)
        return findMusicScoreByMusicId(id)
    }

    private fun findMusicScoreByMusicId(id: Int): ScoreboardResponseDto? {
        return musicRepository.findMusicScoreByMusicId(id)
    }

    override fun getMusicByTitle(title: String): MusicEntity? {
        return musicRepository.findByTitle(title)
    }

    override fun getMusicByArtist(artist: String): MusicEntity? {
        return musicRepository.findByArtist(artist)
    }

    override fun getDailyTopScoreboardByCount(count: Int): List<ScoreboardResponseDto> {
        return musicRepository.findTopMusicScores(PageRequest.of(0,count))
    }

    @Transactional
    @CacheEvict(cacheNames = ["musicCache"], key = "'musicId::' + #id")
    override fun increaseScoreById(id: Int, score: Int): ScoreboardResponseDto? {
        scoreboardRepository.findByMusicId(id)?.let {
            it.score += score
            val music = getMusicById(id)
            eventPublisher.publishEvent(MusicScoreEventDto(music?.title ?: "", music?.artist ?: "", it.score))
        }
        return musicRepository.findMusicScoreByMusicId(id)
    }

}