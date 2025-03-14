package com.example.moviedgskotlin.repositories

import com.example.moviedgskotlin.entities.Review
import org.springframework.data.jpa.repository.JpaRepository

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-10 오전 1:43
 */
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findAllByMovieIdIn(keys: Collection<Long>): List<Review>?
    fun findAllByUserIdIn(keys: Collection<Long>): List<Review>
}