package com.example.moviedgskotlin.datafetchers

import com.example.moviedgskotlin.DgsConstants
import com.example.moviedgskotlin.dataloaders.MoviesByDirectorDataLoader
import com.example.moviedgskotlin.entities.Director
import com.example.moviedgskotlin.entities.Movie
import com.example.moviedgskotlin.exceptions.CustomNotFoundException
import com.example.moviedgskotlin.repositories.MovieRepository
import com.netflix.graphql.dgs.*
import java.util.concurrent.CompletableFuture

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-13 오후 7:12
 */
@DgsComponent
class MovieDataFetcher(
    private val movieRepository: MovieRepository
) {

    @DgsQuery
    fun movies(): MutableList<Movie> {
        return movieRepository.findAll()
    }

    @DgsQuery
    fun movie(
        @InputArgument
        movieId: Long
    ): Movie? {
        return movieRepository.findById(movieId).orElseThrow { CustomNotFoundException("Movie not found") }
    }

    @DgsData(
        parentType = DgsConstants.DIRECTOR.TYPE_NAME,
        field = DgsConstants.DIRECTOR.Movies
    )
    fun getMovieByDirector(
        dfe: DgsDataFetchingEnvironment
    ): CompletableFuture<List<Movie>>? {
        val director = dfe.getSourceOrThrow<Director>()
//        return movieRepository.findByDirectorId(director.id!!)
        val dataloader = dfe.getDataLoader<Long, List<Movie>>(MoviesByDirectorDataLoader::class.java)
        return dataloader.load(director.id!!)
    }

}