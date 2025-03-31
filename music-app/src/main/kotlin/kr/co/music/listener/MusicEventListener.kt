package kr.co.music.listener

import kr.co.music.dto.MusicScoreEventDto
import kr.co.music.util.Log
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MusicEventListener: Log {

    @EventListener
    fun handleMusicScoreUpdatedEvent(event: MusicScoreEventDto) {
        logger.info("The score for the '${event.title} - ${event.artist}' has been updated to ${event.score}!")
    }

}