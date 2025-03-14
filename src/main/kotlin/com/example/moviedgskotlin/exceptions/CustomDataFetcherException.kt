package com.example.moviedgskotlin.exceptions

import com.netflix.graphql.types.errors.ErrorDetail
import com.netflix.graphql.types.errors.ErrorType
import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오후 4:43
 */
@Component
class CustomDataFetcherException : DataFetcherExceptionHandler{

    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        if(handlerParameters.exception is CustomException) {
            val graphqlError = (handlerParameters.exception as CustomException)
                .toGraphQlError(handlerParameters.path, handlerParameters.sourceLocation)
            val result = DataFetcherExceptionHandlerResult.newResult()
                .error(graphqlError)
                .build()
            return CompletableFuture.completedFuture(result)

        } else {

            val graphQLError = TypedGraphQLError
                .newBuilder()
                .errorType(ErrorType.BAD_REQUEST)
                .message(handlerParameters.exception.message ?: "Unknown Error")
                .location(handlerParameters.sourceLocation)
                .path(handlerParameters.path)

                .errorDetail(ErrorDetail.Common.FIELD_NOT_FOUND)
                .debugInfo(mapOf("stackTrace" to handlerParameters.exception.stackTrace.first()))
                .origin("movie-service")
                .debugUri("debug-uri")
                .extensions(mapOf("errorCode" to "1001"))

                .build()

            val result = DataFetcherExceptionHandlerResult.newResult()
                .error(graphQLError)
                .build()

            return CompletableFuture.completedFuture(result)
        }
    }

}