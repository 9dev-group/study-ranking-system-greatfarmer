package kr.co.music.config

import kr.co.music.client.spotify.SpotifyAccountClient
import kr.co.music.client.spotify.SpotifyApiClient
import kr.co.music.client.spotify.dto.Item
import kr.co.music.domain.MusicEntity
import kr.co.music.dto.MusicDto
import kr.co.music.repository.MusicRepository
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class BatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val spotifyAccountClient: SpotifyAccountClient,
    private val spotifyApiClient: SpotifyApiClient,
    private val musicRepository: MusicRepository
) {

    @Value("\${spotify.account.grant-type}")
    lateinit var grantType: String

    @Value("\${spotify.account.client-id}")
    lateinit var clientId: String

    @Value("\${spotify.account.client-secret}")
    lateinit var clientSecret: String

    @Bean
    fun musicBatchJob(): Job {
        return JobBuilder("musicBatchJob", jobRepository)
            .incrementer(RunIdIncrementer())
            .start(musicBatchStep())
            .build()
    }

    @Bean
    fun musicBatchStep(): Step {
        return StepBuilder("musicBatchStep", jobRepository)
            .chunk<Item, MusicDto>(1, transactionManager)
            .reader(getMusicData())
            .processor(trackToMusicDtoProcessor())
            .writer(saveMusicData())
            .build()
    }

    @Bean
    fun getMusicData(): ItemReader<Item> {
        val token = spotifyAccountClient.getToken(
            grantType,
            clientId,
            clientSecret
        )
        val playlists = spotifyApiClient.getPlaylistTracks(
            authorization = "Bearer ${token.accessToken}",
            playlistId = "3cEYpjA9oz9GiPac4AsH4n"
        )

        val iterator = playlists.items.iterator()
        return ItemReader { if (iterator.hasNext()) iterator.next() else null }
    }

    @Bean
    fun trackToMusicDtoProcessor(): ItemProcessor<Item, MusicDto> {
        return ItemProcessor { item ->
            val track = item.track
            val title = track.name
            val artists = track.artists.joinToString(", ") { artist -> artist.name }

            MusicDto(title = title, artist = artists)
        }
    }

    @Bean
    fun saveMusicData(): ItemWriter<MusicDto> {
        return ItemWriter { musicDtos ->
            val entities = musicDtos.map { it.toEntity() }
            musicRepository.saveAll(entities)
        }
    }

    fun MusicDto.toEntity(): MusicEntity {
        return MusicEntity(
            title = this.title,
            artist = this.artist
        )
    }
}