package com.example.moviedgskotlin.exceptions

import com.netflix.graphql.types.errors.ErrorType

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오후 5:21
 */
class CustomNotFoundException(
    override val message: String = "Entity Not Found",
) : CustomException(
    message = message,
    errorType = ErrorType.NOT_FOUND,
    errorCode = 1002
)