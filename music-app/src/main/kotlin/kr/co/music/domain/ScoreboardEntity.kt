package kr.co.music.domain

import jakarta.persistence.*
import kr.co.music.domain.common.BaseEntity
import org.hibernate.annotations.DynamicUpdate

@DynamicUpdate
@Entity
@Table(name = "SCOREBOARD")
class ScoreboardEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scoreboard_id")
    val id: Int = 0,

    @Column(name = "music_id", nullable = false)
    val musicId: Int,

    @Column(name = "score", nullable = false)
    var score: Int
) : BaseEntity() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScoreboardEntity

        if (id != other.id) return false
        if (musicId != other.musicId) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + musicId
        result = 31 * result + score
        return result
    }

    override fun toString(): String {
        return "ScoreboardEntity(id=$id, musicId=$musicId, score=$score)"
    }
}