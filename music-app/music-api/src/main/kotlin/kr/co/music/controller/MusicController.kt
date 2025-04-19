package kr.co.music.controller

import kr.co.music.service.MusicService
import kr.co.music.service.ScoreboardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/music")
class MusicController(
    val musicService: MusicService,
    val scoreboardService: ScoreboardService
) {

    @GetMapping("/id/{id}")
    fun getMusicById(@PathVariable id: Int) =
        ResponseEntity.ok(musicService.getMusicById(id))

    @GetMapping("/title/{title}")
    fun getMusicByTitle(@PathVariable title: String) =
        ResponseEntity.ok(musicService.getMusicByTitle(title))

    @GetMapping("/artist/{artist}")
    fun getMusicByArtist(@PathVariable artist: String) =
        ResponseEntity.ok(musicService.getMusicByArtist(artist))

    @GetMapping("/scoreboard/daily/top/{count}")
    fun getDailyTopScoreboardByCount(@PathVariable count: Int) =
        ResponseEntity.ok(musicService.getDailyTopScoreboardByCount(count))

    @PostMapping("/id/{id}/increase/{score}")
    fun increaseScoreById(@PathVariable id: Int, @PathVariable score: Int) =
        ResponseEntity.ok(musicService.increaseScoreById(id, score))
}