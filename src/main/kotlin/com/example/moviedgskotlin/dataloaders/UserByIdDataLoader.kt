package com.example.moviedgskotlin.dataloaders

import com.example.moviedgskotlin.entities.User
import com.example.moviedgskotlin.repositories.UserRepository
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import org.springframework.beans.factory.annotation.Qualifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오전 1:50
 */
@DgsDataLoader(name = "userById")
class UserByIdDataLoader(
    private val userRepository: UserRepository,
    @Qualifier("DataLoaderThreadPool")
    private val executor: Executor
) : MappedBatchLoader<Long, User> {
    override fun load(keys: MutableSet<Long>): CompletionStage<Map<Long, User>> {
        return CompletableFuture.supplyAsync ({
            userRepository.findAllById(keys)
                .associateBy { it.id!! }
        }, executor)
    }
}