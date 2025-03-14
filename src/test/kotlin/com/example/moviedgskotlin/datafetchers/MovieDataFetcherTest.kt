package com.example.moviedgskotlin.datafetchers

import com.example.moviedgskotlin.customScalars.EmailScalar
import com.example.moviedgskotlin.entities.Movie
import com.example.moviedgskotlin.exceptions.CustomDataFetcherExceptionHandler
import com.example.moviedgskotlin.repositories.MovieRepository
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
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
        EmailScalar::class
    ]
)
class MovieDataFetcherTest {
    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @MockitoBean // SpringBoot 3.4.x 부터 MockitoBean 으로 바뀜
    lateinit var movieRepository: MovieRepository

    @Test
    fun test() {
        // given
        Mockito.`when`(movieRepository.findAll()).thenAnswer {
            listOf(
                Movie(id = 1L, title = "movie1", releaseDate = LocalDate.now()),
                Movie(id = 2L, title = "movie2", releaseDate = LocalDate.now()),
                Movie(id = 3L, title = "movie3", releaseDate = LocalDate.now())
            )
        }

        // when
        val titles: List<String> = dgsQueryExecutor.executeAndExtractJsonPath(
            loadGraphQLQuery("graphql/movies.graphql"),
            "data.movies[*].title"
        )

        assertThat(titles).contains("movie1")
        assertThat(titles).hasSize(3)
    }

    @Test
    fun movie() {
        // given
        val givenMovie = Movie(id = 1L, title = "movie1", releaseDate = LocalDate.now())
        Mockito.`when`(movieRepository.findById(1L)).thenAnswer {
            Optional.of(givenMovie)
        }

        // when
        val movie: String = dgsQueryExecutor.executeAndExtractJsonPath(
            loadGraphQLQuery("graphql/movie.graphql"),
            "data.movie.title"
        )

        // then
        assertThat(movie).isEqualTo(givenMovie.title)
    }

    @Test
    fun movieWithException() {
        // given
        Mockito.`when`(movieRepository.findById(1L)).thenAnswer {
            Optional.ofNullable<Movie>(null)
        }

        // when
        val result = dgsQueryExecutor.execute(
            loadGraphQLQuery("graphql/movie.graphql")
        )

        // then
        assertThat(result.errors).isNotEmpty
        assertThat(result.errors[0].message).contains("Movie not found")
        assertThat(result.errors[0].extensions["errorCode"]).isEqualTo(1002)
    }

    fun loadGraphQLQuery(filename: String): String {
        val classLoader = Thread.currentThread().contextClassLoader
        return classLoader.getResourceAsStream(filename)?.bufferedReader()?.use { it.readText() }
            ?: throw IllegalArgumentException("파일을 찾을 수 없음: $filename")
    }
}