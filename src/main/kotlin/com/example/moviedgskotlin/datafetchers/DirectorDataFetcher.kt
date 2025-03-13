package com.example.moviedgskotlin.datafetchers

import com.example.moviedgskotlin.DgsConstants
import com.example.moviedgskotlin.dataloaders.DirectorByIdDataLoader
import com.example.moviedgskotlin.entities.Director
import com.example.moviedgskotlin.entities.Movie
import com.example.moviedgskotlin.repositories.DirectorRepository
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-13 오후 9:00
 */
@DgsComponent
class DirectorDataFetcher(
    private val directorRepository: DirectorRepository
) {

    @DgsData(
        parentType = DgsConstants.MOVIE.TYPE_NAME,
        field = DgsConstants.MOVIE.Director
    )
    fun getDirectorByMovie(
        dfe: DgsDataFetchingEnvironment
    ): CompletableFuture<Director>? {
//        val movie = dfe.getSource<Movie>() ?: { NoSuchElementException("source is null") }
        val movie = dfe.getSourceOrThrow<Movie>()
//        return directorRepository.findById(movie.director?.id!!).get()

        // dataloader 사용
        val dataloader = dfe.getDataLoader<Long, Director>(DirectorByIdDataLoader::class.java)
        return dataloader.load(movie.director?.id!!)
    }

}