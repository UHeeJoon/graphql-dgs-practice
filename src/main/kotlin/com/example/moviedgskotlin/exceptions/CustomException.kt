package com.example.moviedgskotlin.exceptions

import com.netflix.graphql.types.errors.ErrorType
import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.execution.ResultPath
import graphql.language.SourceLocation

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오후 5:16
 */
abstract class CustomException(
    override val message: String,
    override val cause: Exception? = null,
    val errorType: ErrorType = ErrorType.UNKNOWN,
    val errorCode: Int = 1000,
) : RuntimeException(message, cause) {

    companion object {
        const val EXTENSION_CLASS_KEY = "class"
        const val EXTENSION_ERROR_CODE = "errorCode"
        const val EXTENSION_STACK_TRACE = "stackTrace"
    }

    fun toGraphQlError(
        path: ResultPath? = null,
        location: SourceLocation? = null,
    ): TypedGraphQLError {
        return TypedGraphQLError
            .newBuilder()
            .apply {
                if (path != null) {
                    path(path)
                }
                if (location != null) {
                    location(location)
                }
            }
            .errorType(errorType)
            .message(message)
            .extensions(
                mapOf(
                    EXTENSION_CLASS_KEY to this::class.java.name,
                    EXTENSION_ERROR_CODE to errorCode,
                    EXTENSION_STACK_TRACE to stackTrace.firstOrNull()
                )
            )
            .build()
    }
}