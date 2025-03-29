package kr.co.music.domain

import jakarta.persistence.*
import kr.co.music.domain.common.BaseEntity

@Entity
@Table(name = "MUSIC")
class MusicEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    val musicId: Int = 0,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "artist", nullable = false)
    val artist: String
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MusicEntity

        if (musicId != other.musicId) return false
        if (title != other.title) return false
        if (artist != other.artist) return false

        return true
    }

    override fun hashCode(): Int {
        var result = musicId?.hashCode() ?: 0
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        return result
    }

    override fun toString(): String {
        return "MusicEntity(musicId=$musicId, title='$title', artist='$artist')"
    }
}