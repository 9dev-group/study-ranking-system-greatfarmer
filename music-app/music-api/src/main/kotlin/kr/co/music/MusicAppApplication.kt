package kr.co.music

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@SpringBootApplication
class MusicAppApplication

fun main(args: Array<String>) {
	runApplication<MusicAppApplication>(*args)
}
