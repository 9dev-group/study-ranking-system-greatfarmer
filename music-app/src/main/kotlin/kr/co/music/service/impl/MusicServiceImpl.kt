package kr.co.music.service.impl

import kr.co.music.domain.MusicEntity
import kr.co.music.dto.ScoreboardResponseDto
import kr.co.music.repository.MusicRepository
import kr.co.music.service.MusicService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class MusicServiceImpl(
    private val musicRepository: MusicRepository
): MusicService {
    override fun getMusicById(id: Int): MusicEntity? {
        return musicRepository.findByMusicId(id)
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

}