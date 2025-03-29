package kr.co.music.repository

import kr.co.music.domain.ScoreboardEntity
import kr.co.music.repository.custom.ScoreboardRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ScoreboardRepository: JpaRepository<ScoreboardEntity, Long>, ScoreboardRepositoryCustom {
    fun findByMusicId(musicId: Int): ScoreboardEntity?
}