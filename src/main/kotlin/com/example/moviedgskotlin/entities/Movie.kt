package com.example.moviedgskotlin.entities

import jakarta.persistence.*
import java.time.LocalDate

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-10 오전 1:38
 */
@Entity
class Movie(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val title: String? = null,

    @Column(nullable = false)
    val releaseDate: LocalDate? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "director_id", nullable = false)
    val director: Director? = null,

    @OneToMany(mappedBy = "movie")
    val reviews:  List<Review>? = emptyList()

)