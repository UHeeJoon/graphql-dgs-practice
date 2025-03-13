package com.example.moviedgskotlin.entities

import jakarta.persistence.*

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-10 오전 1:39
 */
@Entity
class Review(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val rating: Int? = null,

    @Column(nullable = false)
    val comment: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    val movie: Movie? = null,

) {

}