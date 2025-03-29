package kr.co.music.service.impl

import kr.co.music.domain.MusicEntity
import kr.co.music.repository.MusicRepository
import kr.co.music.service.MusicService
import org.springframework.stereotype.Service

@Service
class MusicServiceImpl(
    private val musicRepository: MusicRepository
): MusicService {

    override fun getMusicByTitle(title: String): MusicEntity? {
        return musicRepository.findByTitle(title)
    }

}