package kr.co.music.service

import kr.co.music.domain.MusicEntity
import kr.co.music.dto.ScoreboardResponseDto

interface MusicService {

    fun getMusicById(id: Int): MusicEntity?
    fun getMusicByTitle(title: String): MusicEntity?
    fun getMusicByArtist(artist: String): MusicEntity?
    fun getDailyTopScoreboardByCount(count: Int): List<ScoreboardResponseDto>
}