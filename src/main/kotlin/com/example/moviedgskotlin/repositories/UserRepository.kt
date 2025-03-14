package com.example.moviedgskotlin.repositories

import com.example.moviedgskotlin.entities.User
import org.springframework.data.jpa.repository.JpaRepository

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-10 오전 1:43
 */
interface UserRepository:  JpaRepository<User, Long>