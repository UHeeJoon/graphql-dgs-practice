package com.example.moviedgskotlin.dataloaders

import com.example.moviedgskotlin.entities.Movie
import com.example.moviedgskotlin.repositories.MovieRepository
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
 * @date        :2025-03-14 오전 1:36
 */
@DgsDataLoader(name = "moviesByDirectorId")
class MoviesByDirectorDataLoader(
    private val movieRepository: MovieRepository,
    @Qualifier("DataLoaderThreadPool")
    private val executor: Executor
) : MappedBatchLoader<Long, List<Movie>> {
    override fun load(keys: MutableSet<Long>): CompletionStage<Map<Long, List<Movie>>> {
        return CompletableFuture.supplyAsync ({
            movieRepository
                .findAllByDirectorIdIn(keys)
                ?.groupBy { it.director?.id!! }
        }, executor)
    }
}