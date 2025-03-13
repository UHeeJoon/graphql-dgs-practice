package com.example.moviedgskotlin.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-09 오후 10:22
 */
@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false)
    val username: String? = null,
    @Column(nullable = false)
    val email: String? = null,

    @OneToMany(mappedBy = "user")
    val reviews: List<Review>? = emptyList()

) {
}