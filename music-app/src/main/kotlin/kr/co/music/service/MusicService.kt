package kr.co.music.service

import kr.co.music.domain.MusicEntity

interface MusicService {
    fun getMusicByTitle(title: String): MusicEntity?
}