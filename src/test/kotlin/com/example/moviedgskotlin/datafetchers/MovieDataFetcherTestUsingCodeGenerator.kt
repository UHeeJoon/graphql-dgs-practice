package com.example.moviedgskotlin.datafetchers

import com.example.moviedgskotlin.client.MovieGraphQLQuery
import com.example.moviedgskotlin.client.MovieProjectionRoot
import com.example.moviedgskotlin.config.DataLoaderExecutor
import com.example.moviedgskotlin.customScalars.EmailScalar
import com.example.moviedgskotlin.dataloaders.DirectorByIdDataLoader
import com.example.moviedgskotlin.entities.Director
import com.example.moviedgskotlin.entities.Movie
import com.example.moviedgskotlin.exceptions.CustomDataFetcherExceptionHandler
import com.example.moviedgskotlin.repositories.DirectorRepository
import com.example.moviedgskotlin.repositories.MovieRepository
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import com.netflix.graphql.dgs.test.EnableDgsTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import java.util.*

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오후 5:51
 */
@EnableDgsTest
@SpringBootTest(
    classes = [
        MovieDataFetcher::class,
        DgsExtendedScalarsAutoConfiguration::class,
        CustomDataFetcherExceptionHandler::class,

        DirectorDataFetcher::class,
        DirectorByIdDataLoader::class,
        DataLoaderExecutor::class,

        EmailScalar::class
    ]
)
class MovieDataFetcherTestUsingCodeGenerator {
    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockitoBean // SpringBoot 3.4.x 부터 MockitoBean 으로 바뀜
    lateinit var movieRepository: MovieRepository

    @MockitoBean
    lateinit var directorRepository: DirectorRepository

    @Test
    fun movieWithQueryRequest() {
        // given
        Mockito.`when`(movieRepository.findById(1L)).thenAnswer {
            Optional.of(Movie(id = 1L, title = "movie1", releaseDate = LocalDate.now()))
        }

        // when
        val graphQLQueryRequest = GraphQLQueryRequest(
            MovieGraphQLQuery.Builder()
                .movieId(1)
                .build(),
            MovieProjectionRoot<Nothing, Nothing>().title()
        )

        val title: String = dgsQueryExecutor.executeAndExtractJsonPath(
            graphQLQueryRequest.serialize(),
            "data.movie.title"
        )

        // then
        assertThat(title).isEqualTo("movie1")
    }

    @Test
    fun movieWithDirector() {

        //given
        Mockito.`when`(movieRepository.findById(1L)).thenAnswer {
            Optional.of(Movie(id = 1L, title = "movie1", releaseDate = LocalDate.now(), director = Director(1)))
        }
        Mockito.`when`(directorRepository.findAllById(listOf(1L))).thenAnswer {
            listOf(Director(1L, name = "director1"))
        }

        // when
        val graphQLQueryRequest = GraphQLQueryRequest(
            MovieGraphQLQuery.Builder()
                .movieId(1)
                .build(),
            MovieProjectionRoot<Nothing, Nothing>()
                .director()
                .name()
                .id()
        )

        val directorName: String = dgsQueryExecutor.executeAndExtractJsonPath(
            graphQLQueryRequest.serialize(),
            "data.movie.director.name"
        )

        // then
        assertThat(directorName).isEqualTo("director1")
    }
}