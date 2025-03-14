package com.example.moviedgskotlin.dataloaders

import com.example.moviedgskotlin.entities.Review
import com.example.moviedgskotlin.repositories.ReviewRepository
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
 * @date        :2025-03-14 오전 1:57
 */
@DgsDataLoader(name = "reviewsByUser")
class ReviewsByUserDataLoader(
    private val reviewRepository: ReviewRepository,
    @Qualifier("DataLoaderThreadPool")
    private val executor: Executor
) : MappedBatchLoader<Long, List<Review>> {

    override fun load(keys: MutableSet<Long>): CompletionStage<Map<Long, List<Review>>> {
        return CompletableFuture.supplyAsync({
            reviewRepository
                .findAllByUserIdIn(keys)
                .groupBy { it.user?.id!! }

        }, executor)
    }
}