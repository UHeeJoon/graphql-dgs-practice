package com.example.moviedgskotlin.datafetchers

import com.example.moviedgskotlin.DgsConstants
import com.example.moviedgskotlin.entities.Movie
import com.example.moviedgskotlin.entities.Review
import com.example.moviedgskotlin.entities.User
import com.example.moviedgskotlin.repositories.ReviewRepository
import com.example.moviedgskotlin.types.AddReviewInput
import com.netflix.graphql.dgs.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.util.concurrent.Queues

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-13 오후 7:58
 */
@DgsComponent
class ReviewDataFetcher(
    private val userDataFetcher: UserDataFetcher,
    private val movieDataFetcher: MovieDataFetcher,
    private val reviewRepository: ReviewRepository
) {

    @DgsMutation
    fun addReview(
        @InputArgument
        input: AddReviewInput
    ): Review {
        val user = userDataFetcher.user(input.userId)
        val movie = movieDataFetcher.movie(input.movieId)
        val review = Review(
            user = user,
            movie = movie,
            rating = input.rating,
            comment = input.comment
        )
        reviewRepository.save(review)

        reviewSink.tryEmitNext(review)

        return review
    }

    private val reviewSink = Sinks
        .many()
        .multicast()
        .onBackpressureBuffer<Review>(Queues.SMALL_BUFFER_SIZE, false)

    @DgsSubscription
    fun newReview(
        @InputArgument
        movieId: Long
    ): Flux<Review> {
        return reviewSink.asFlux()
            .filter { it.movie?.id == movieId }
    }

    @DgsData(
        parentType = DgsConstants.MOVIE.TYPE_NAME,
        field = DgsConstants.MOVIE.Reviews
    )
    fun getReviewsByMovie(dfe: DgsDataFetchingEnvironment): List<Review> {
        val movie = dfe.getSourceOrThrow<Movie>()
        return reviewRepository.findByMovieId(movie.id!!)
    }

    @DgsData(
        parentType = DgsConstants.USER.TYPE_NAME,
        field = DgsConstants.USER.Reviews
    )
    fun getReviewsByUser(dfe: DgsDataFetchingEnvironment): List<Review> {
        val user = dfe.getSourceOrThrow<User>()
        return reviewRepository.findByUserId(user.id!!)
    }

}