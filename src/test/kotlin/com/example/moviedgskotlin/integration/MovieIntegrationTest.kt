package com.example.moviedgskotlin.integration

import com.example.moviedgskotlin.exceptions.CustomNotFoundException
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * please explain class!
 *
 * @author       :Uheejoon
 * @date        :2025-03-14 오후 7:27
 */
@SpringBootTest
@AutoConfigureMockMvc
class MovieIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `movie integration test`() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(Query(loadFile("graphql/movie-integration.graphql")))
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                MockMvcResultMatchers.content().json(
                    loadFile("json/movie-integration-response.json")
                )
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `movie 없는 영화 조회`() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(Query(loadFile("graphql/movie-not-found-exception-integration.graphql")))
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("errors[0].message").value("Movie not found"))
            .andExpect(MockMvcResultMatchers.jsonPath("errors[0].extensions.class").value(CustomNotFoundException::class.java.name))
            .andExpect(MockMvcResultMatchers.jsonPath("errors[0].extensions.errorCode").value(1002))
            .andExpect(
                MockMvcResultMatchers.content().json(
                    loadFile("json/movie-not-found-exception-integration-response.json")
                )
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `movies integration test`() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(Query(loadFile("graphql/movies-integration.graphql")))
                )
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                MockMvcResultMatchers.content().json(
                    loadFile("json/movies-integration-response.json")
                )
            )
            .andDo(MockMvcResultHandlers.print())
    }

    fun loadFile(filename: String): String {
        val classLoader = Thread.currentThread().contextClassLoader
        return classLoader.getResourceAsStream(filename)?.bufferedReader()?.use { it.readText() }
            ?: throw IllegalArgumentException("파일을 찾을 수 없음: $filename")
    }
    
    data class Query(val query: String)
}