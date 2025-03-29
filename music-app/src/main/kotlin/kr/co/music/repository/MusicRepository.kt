package kr.co.music.repository

import kr.co.music.domain.MusicEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MusicRepository: JpaRepository<MusicEntity, Long> {
    fun findByTitle(title: String): MusicEntity?
}