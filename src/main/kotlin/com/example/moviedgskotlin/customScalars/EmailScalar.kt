package com.example.moviedgskotlin.customScalars

import com.netflix.graphql.dgs.DgsScalar
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.util.*

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-09 오후 9:35
 */

@DgsScalar(name = "Email")
class EmailScalar: Coercing<String, String> {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String? {
        val email = dataFetcherResult.toString()
        if (emailRegex.matches(email)) {
            return email
        }
        throw CoercingSerializeException("Invalid email format: $email")
    }

    // variable을 사용해서 보낼 때
    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): String? {
        val email = input.toString()
        if(emailRegex.matches(email)) {
            return email
        }
        throw CoercingParseValueException("Invalid email format: $email")
    }

    // 필드로 보낼 때
    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale
    ): String? {
        val email = (input as StringValue).value
        if(emailRegex.matches(email)) {
            return email
        }
        throw CoercingParseLiteralException("Invalid email format: $email")
    }
}