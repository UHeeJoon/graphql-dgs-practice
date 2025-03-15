package com.example.moviedgskotlin.monitoring

import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimplePerformantInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-15 오후 5:45
 */
@Component
class MovieLoggingInstrumentation : SimplePerformantInstrumentation() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // state를 초기화 하는 객체 - 초기화 한 객체를 모든 단계에서 공유할 수 있다.
    override fun createState(parameters: InstrumentationCreateStateParameters?): InstrumentationState? {
        return LoggingState()
    }

    // 쿼리가 실행 되기 전에 호출 되며, 필요한 사전 작업을 실행할 수 있다.
    override fun beginExecution(
        parameters: InstrumentationExecutionParameters,
        state: InstrumentationState
    ): InstrumentationContext<ExecutionResult>? {
        require(state is LoggingState)
        state.operationName = state.operationName
        state.requestQuery = parameters.query
        return super.beginExecution(parameters, state)
    }

    // 실행 후의 결과값 제어
    override fun instrumentExecutionResult(
        executionResult: ExecutionResult,
        parameters: InstrumentationExecutionParameters,
        state: InstrumentationState
    ): CompletableFuture<ExecutionResult> {
        require(state is LoggingState)
        state.responseData = executionResult.getData<Any>()?.toString()
        state.errorMessage = executionResult.errors.joinToString { it.toString() }
        state.log(logger)
        return super.instrumentExecutionResult(executionResult, parameters, state)
    }

    data class LoggingState(
        var startTime: Long = System.currentTimeMillis(),
        var operationName: String? = null,
        var requestQuery: String? = null,
        var responseData: String? = null,
        var errorMessage: String? = null,
    ) : InstrumentationState {
        fun log(logger: Logger) {
            logger.info(
                "\n[logging]\n" +
                        "Operation Name: ${operationName}\n" +
                        "Request Query: ${requestQuery}\n" +
                        "Response Data: ${responseData}\n" +
                        "Error Message: ${errorMessage}\n" +
                        "Total Time: ${System.currentTimeMillis() - startTime}ms\n"
            )
        }
    }

}