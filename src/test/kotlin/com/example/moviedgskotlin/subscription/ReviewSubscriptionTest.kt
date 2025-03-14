package com.example.moviedgskotlin.subscription

import com.example.moviedgskotlin.customScalars.EmailScalar
import com.example.moviedgskotlin.datafetchers.MovieDataFetcher
import com.example.moviedgskotlin.datafetchers.ReviewDataFetcher
import com.example.moviedgskotlin.datafetchers.UserDataFetcher
import com.example.moviedgskotlin.entities.Movie
import com.example.moviedgskotlin.entities.Review
import com.example.moviedgskotlin.entities.User
import com.example.moviedgskotlin.repositories.ReviewRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import com.netflix.graphql.dgs.test.EnableDgsTest
import graphql.ExecutionResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오후 8:20
 */
@SpringBootTest(
    classes = [
        DgsExtendedScalarsAutoConfiguration::class,
        ReviewDataFetcher::class,
        EmailScalar::class
    ]
)
@EnableDgsTest
class ReviewSubscriptionTest {
    @MockitoBean
    lateinit var userDataFetcher: UserDataFetcher

    @MockitoBean
    lateinit var movieDataFetcher: MovieDataFetcher

    @MockitoBean
    lateinit var reviewRepository: ReviewRepository

    @Autowired
    lateinit var dgsQueryExecutor: DgsQueryExecutor

    @Test
    fun newReview() {
        // Publisher
        val executeResult = dgsQueryExecutor.execute(
            loadFile("graphql/newReview-subscription.graphql")
        )

        val reviewPublisher = executeResult.getData<Publisher<ExecutionResult>>()
        val reviews = mutableListOf<Review>()

        addReview()
        addReview()

        reviewPublisher.subscribe(
            object : Subscriber<ExecutionResult> {
                override fun onSubscribe(s: Subscription) {
                    s.request(2L)
                }

                override fun onComplete() {
                    println("Complete!!")
                }

                override fun onNext(t: ExecutionResult) {
                    val data = t.getData<Map<String, Any>>()

                    reviews.add(ObjectMapper().convertValue(data["newReview"], Review::class.java))
                }

                override fun onError(t: Throwable) {
                    println("Error : ${t.message}")
                }
            }
        )

        assertThat(reviews).hasSize(2)
        Mockito.verify(reviewRepository, Mockito.times(2)).save(Mockito.any(Review::class.java))
    }

    private fun addReview() {
        Mockito.`when`(movieDataFetcher.movie(101L)).thenAnswer { Movie(id = 101L) }
        Mockito.`when`(userDataFetcher.user(101L)).thenAnswer { User(id = 101L) }

        dgsQueryExecutor.execute(
            loadFile("graphql/addReview-mutation.graphql")
        )

    }

    private fun loadFile(filename: String): String {
        val classLoader = Thread.currentThread().contextClassLoader
        return classLoader.getResourceAsStream(filename)?.bufferedReader()?.use { it.readText() }
            ?: throw IllegalArgumentException("파일을 찾을 수 없음: $filename")
    }

}