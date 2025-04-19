package kr.co.music.domain.common

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@MappedSuperclass
abstract class BaseEntity {
    @CreationTimestamp
    @Column(name = "ins_date", updatable = false)
    val insDate: Instant? = null

    @Column(name = "ins_oprt", nullable = false)
    var insOprt: String = "anonymous"

    @UpdateTimestamp
    @Column(name = "upd_date")
    val updDate: Instant? = null

    @Column(name = "upd_oprt", nullable = false)
    var updOprt: String = "anonymous"
}