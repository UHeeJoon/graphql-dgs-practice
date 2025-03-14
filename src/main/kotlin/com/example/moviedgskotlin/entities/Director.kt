package com.example.moviedgskotlin.entities

import jakarta.persistence.*

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-10 오전 1:37
 */
@Entity
class Director(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val name: String? = null,

    @OneToMany(mappedBy = "director")
    val movies: List<Movie>? = emptyList()

)