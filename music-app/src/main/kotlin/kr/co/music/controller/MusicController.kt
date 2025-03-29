package kr.co.music.controller

import kr.co.music.service.MusicService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/music")
class MusicController(
    val musicService: MusicService
) {

    @GetMapping("/title/{title}")
    fun getMusicByTitle(@PathVariable title: String) =
        ResponseEntity.ok(musicService.getMusicByTitle(title))
}