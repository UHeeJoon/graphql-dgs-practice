package com.example.moviedgskotlin.datafetchers

import com.example.moviedgskotlin.DgsConstants
import com.example.moviedgskotlin.dataloaders.ReviewsByMovieDataLoader
import com.example.moviedgskotlin.dataloaders.ReviewsByUserDataLoader
import com.example.moviedgskotlin.entities.Movie
import com.example.moviedgskotlin.entities.Review
import com.example.moviedgskotlin.entities.User
import com.example.moviedgskotlin.repositories.ReviewRepository
import com.example.moviedgskotlin.types.AddReviewInput
import com.netflix.graphql.dgs.*
import jakarta.annotation.PreDestroy
import org.springframework.util.StringUtils
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.util.concurrent.Queues
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID
import java.util.concurrent.CompletableFuture

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

    val tempDir: Path = Files.createTempDirectory("review_images")

    // 서버가 종료될 때 생성한 파일 전체 삭제
    @PreDestroy
    fun cleanUp() {
        Files.walk(tempDir)
            .map { it.toFile() }
            .forEach { it.delete() }
    }

    @DgsMutation
    fun addReview(
        @InputArgument
        input: AddReviewInput
    ): Review {
        val user = userDataFetcher.user(input.userId)
        val movie = movieDataFetcher.movie(input.movieId)

        // img upload
        val imageFileUrl = input.imageFile
            ?.let { imageFile ->
                // 파일명 생성
                val fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(imageFile.originalFilename!!)
                val targetLocation = tempDir.resolve(fileName)

                // 파일 저장
                Files.copy(imageFile.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)

                targetLocation.toString()
            }

        val review = Review(
            user = user,
            movie = movie,
            rating = input.rating,
            comment = input.comment,
            imageFileUrl = imageFileUrl
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
    fun getReviewsByMovie(
        dfe: DgsDataFetchingEnvironment
    ): CompletableFuture<List<Review>>? {
        val movie = dfe.getSourceOrThrow<Movie>()
//        return reviewRepository.findByMovieId(movie.id!!)

        val dataloader = dfe.getDataLoader<Long, List<Review>>(ReviewsByMovieDataLoader::class.java)
        return dataloader.load(movie.id!!)
    }

    @DgsData(
        parentType = DgsConstants.USER.TYPE_NAME,
        field = DgsConstants.USER.Reviews
    )
    fun getReviewsByUser(dfe: DgsDataFetchingEnvironment): CompletableFuture<List<Review>>? {
        val user = dfe.getSourceOrThrow<User>()
//        return reviewRepository.findByUserId(user.id!!)
        val dataloader = dfe.getDataLoader<Long, List<Review>>(ReviewsByUserDataLoader::class.java)
        return dataloader.load(user.id!!)
            .thenApply { it ?: emptyList()}
    }

}