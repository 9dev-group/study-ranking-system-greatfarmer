package kr.co.music.dto

data class ScoreboardResponseDto(
    val musicId: Int,
    val title: String,
    val artist: String,
    val score: Int
)