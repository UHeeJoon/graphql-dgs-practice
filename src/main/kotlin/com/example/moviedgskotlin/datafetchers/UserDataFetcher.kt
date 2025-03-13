package com.example.moviedgskotlin.datafetchers

import com.example.moviedgskotlin.DgsConstants
import com.example.moviedgskotlin.dataloaders.UserByIdDataLoader
import com.example.moviedgskotlin.entities.Review
import com.example.moviedgskotlin.entities.User
import com.example.moviedgskotlin.repositories.UserRepository
import com.example.moviedgskotlin.types.AddUserInput
import com.netflix.graphql.dgs.*
import java.util.concurrent.CompletableFuture

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-13 오후 7:30
 */
@DgsComponent
class UserDataFetcher(
    private val userRepository: UserRepository
) {

    @DgsQuery
    fun user(
        @InputArgument userId: Long
    ): User? {
        return userRepository.findById(userId).orElseThrow { NoSuchElementException("User not found") }
    }

    @DgsMutation
    fun addUser(
        @InputArgument input: AddUserInput
    ): User {
        val user = User(
            username = input.username,
            email = input.email
        )
        return userRepository.save(user)
    }

    @DgsData(
        parentType = DgsConstants.REVIEW.TYPE_NAME,
        field = DgsConstants.REVIEW.User
    )
    fun getUserByReview(dfe: DgsDataFetchingEnvironment): CompletableFuture<User>? {
        val review = dfe.getSourceOrThrow<Review>()
//        return userRepository.findById(review.user?.id!!).orElseThrow { NoSuchElementException("User not found") }
        val dataloader = dfe.getDataLoader<Long, User>(UserByIdDataLoader::class.java)
        return dataloader.load(review.user?.id!!)
    }
}