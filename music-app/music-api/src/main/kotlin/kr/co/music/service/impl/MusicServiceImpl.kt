package kr.co.music.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
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
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MusicServiceImpl(
    private val musicRepository: MusicRepository,
    private val scoreboardRepository: ScoreboardRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
): MusicService, Log {

    companion object {
        private const val CACHE_REDIS_KEY = "musicCache"
        private const val RANKING_REDIS_KEY = "musicRanking"
    }

    // @Retryable, @Recover 사용
//    @Retryable(maxAttempts = 1)
//    @Cacheable(cacheNames = [CACHE_REDIS_KEY], key = "'musicId::' + #id")
//    override fun getMusicById(id: Int): ScoreboardResponseDto? {
//        return findMusicScoreByMusicId(id)
//    }
//
//    @Recover
//    fun recoverGetMusicById(e: Exception, id: Int): ScoreboardResponseDto? {
//        logger.warn("Redis access failed. Fallback to DB. cause: ${e.message}")
//        return findMusicScoreByMusicId(id)
//    }

    // CircuitBreaker 사용
    @CircuitBreaker(name = "redis-circuitbreaker", fallbackMethod = "fallbackFindMusicScoreByMusicId")
    @Cacheable(cacheNames = [CACHE_REDIS_KEY], key = "'musicId::' + #id")
    override fun getMusicById(id: Int): ScoreboardResponseDto? {
        val result = findMusicScoreByMusicId(id)
        result?.score?.let { setMusicRankingByIdUsingRedis(id, it) }
        return result
    }

    private fun fallbackFindMusicScoreByMusicId(id: Int, e: Throwable): ScoreboardResponseDto? {
        logger.warn("Redis access failed. Fallback to DB. cause: ${e.message}")
        return findMusicScoreByMusicId(id)
    }

    private fun findMusicScoreByMusicId(id: Int): ScoreboardResponseDto? {
        return musicRepository.findMusicScoreByMusicId(id) ?: throw NotFoundException()
    }

    override fun getMusicByTitle(title: String): MusicEntity? {
        return musicRepository.findByTitle(title)
    }

    override fun getMusicByArtist(artist: String): MusicEntity? {
        return musicRepository.findByArtist(artist)
    }

    @CircuitBreaker(name = "redis-circuitbreaker", fallbackMethod = "fallbackGetDailyTopScoreboardByCount")
    override fun getDailyTopScoreboardByCount(count: Int): List<ScoreboardResponseDto> {
        return getDailyTopScoreboardByCountUsingRedis(count)
            .mapNotNull { key ->
                redisTemplate.opsForValue().get(key)?.let {
                    convertToScoreboardDto(it)
                }
            }
    }

    private fun convertToScoreboardDto(json: String): ScoreboardResponseDto? {
        return try {
            objectMapper.readValue(json, ScoreboardResponseDto::class.java)
        } catch (e: Exception) {
            logger.warn("Redis access failed. Fallback to DB. cause: ${e.message}")
            null
        }
    }

    private fun fallbackGetDailyTopScoreboardByCount(count: Int, e: Throwable): List<ScoreboardResponseDto> {
        logger.warn("Redis access failed. Fallback to DB. cause: ${e.message}")
        return musicRepository.findTopMusicScores(PageRequest.of(0, count))
    }

    @Transactional
    @CacheEvict(cacheNames = [CACHE_REDIS_KEY], key = "'musicId::' + #id")
    override fun increaseScoreById(id: Int, score: Int): ScoreboardResponseDto? {
        scoreboardRepository.findByMusicId(id)?.let {
            it.score += score
            setMusicRankingByIdUsingRedis(id, score)
            val music = getMusicById(id)
            eventPublisher.publishEvent(MusicScoreEventDto(music?.title ?: "", music?.artist ?: "", it.score))
        }
        return musicRepository.findMusicScoreByMusicId(id)
    }

    private fun setMusicRankingByIdUsingRedis(id: Int, score: Int) {
        redisTemplate.opsForZSet().add(RANKING_REDIS_KEY,"$CACHE_REDIS_KEY::musicId::$id", score.toDouble())
    }

    private fun getDailyTopScoreboardByCountUsingRedis(count: Int): Set<String> {
        return redisTemplate.opsForZSet().reverseRange(RANKING_REDIS_KEY, 0, ((count - 1).toLong())) ?: emptySet()
    }
}