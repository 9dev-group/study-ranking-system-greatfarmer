package kr.co.music.repository

import kr.co.music.domain.MusicEntity
import kr.co.music.dto.ScoreboardResponseDto
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MusicRepository: JpaRepository<MusicEntity, Long> {
    fun findByMusicId(musicId: Int): MusicEntity?
    fun findByTitle(title: String): MusicEntity?
    fun findByArtist(artist: String): MusicEntity?

    @Query("""
       SELECT NEW kr.co.music.dto.ScoreboardResponseDto(m.musicId, m.title, m.artist, s.score)
       FROM MusicEntity m
       JOIN ScoreboardEntity s ON m.musicId = s.musicId
       ORDER BY s.score DESC
    """)
    fun findTopMusicScores(pageable: Pageable): List<ScoreboardResponseDto>

    @Query("""
       SELECT NEW kr.co.music.dto.ScoreboardResponseDto(m.musicId, m.title, m.artist, s.score)
       FROM MusicEntity m
       JOIN ScoreboardEntity s ON m.musicId = s.musicId
       WHERE m.musicId = :musicId
    """)
    fun findMusicScoreByMusicId(musicId: Int): ScoreboardResponseDto?
}