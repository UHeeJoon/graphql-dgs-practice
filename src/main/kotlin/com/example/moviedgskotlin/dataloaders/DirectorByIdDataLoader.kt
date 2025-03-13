package com.example.moviedgskotlin.dataloaders

import com.example.moviedgskotlin.entities.Director
import com.example.moviedgskotlin.repositories.DirectorRepository
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.BatchLoader
import org.springframework.beans.factory.annotation.Qualifier
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오전 1:14
 */
@DgsDataLoader(name = "directorById")
class DirectorByIdDataLoader(
    private val directorRepository: DirectorRepository,
    @Qualifier("DataLoaderThreadPool")
    private val executor: Executor
) : BatchLoader<Long, Director> {
    override fun load(keys: MutableList<Long>): CompletionStage<List<Director>> {
        return CompletableFuture.supplyAsync(
            { directorRepository.findAllById(keys) },
            executor
        )
    }
}