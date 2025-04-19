package kr.co.music

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["kr.co.music.client"])
class MusicAppApplication

fun main(args: Array<String>) {
	runApplication<MusicAppApplication>(*args)
}
