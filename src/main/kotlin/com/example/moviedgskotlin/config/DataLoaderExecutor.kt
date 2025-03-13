package com.example.moviedgskotlin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오전 2:08
 */
@Configuration
class DataLoaderExecutor {
    @Bean(name = ["DataLoaderThreadPool"])
    fun dataLoaderExecutor(): Executor {
        return Executors.newCachedThreadPool()
    }
}