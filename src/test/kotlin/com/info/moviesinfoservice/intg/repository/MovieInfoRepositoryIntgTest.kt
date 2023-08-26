package com.info.moviesinfoservice.intg.repository

import com.info.moviesinfoservice.domain.MovieInfo
import com.info.moviesinfoservice.domain.MovieInfoRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDate
import java.util.function.Consumer

// embedded 몽고디비를 검증하는 통합 테스트 어노테이션
// 내장 몽고디비를 통해 검증이 가능하다.
@DataMongoTest
@ActiveProfiles("test")
class MovieInfoRepositoryIntgTest {

    @Autowired
    private lateinit var movieInfoRepository: MovieInfoRepository


    @BeforeEach
    fun init(){
        val movieInfos: List<MovieInfo> = java.util.List.of<MovieInfo>(
            MovieInfo(
                "a",
                "배트맨 비긴스",
                2005,
                mutableListOf("C크리스찬 베일", "마이클 케인"),
                LocalDate.parse("2005-06-15")
            ),
            MovieInfo(
                "b",
                "다크나이트",
                2008,
                mutableListOf("C크리스찬 베일", "히스 레저"),
                LocalDate.parse("2008-07-18")
            ),
            MovieInfo(
                "c",
                "다크나이트 라이즈",
                2012,
                mutableListOf("크리스찬 베일", "톰 하디"),
                LocalDate.parse("2012-07-20")
            )
        )
        // 이것도 비동기 함수이기 때문에
        // 블락을 안 해주면 stream 함수 영향으로
        // 아래 테스트 함수가 저장함수가 끝나기 전에 실행될 수 있다.
        // 이것도 비동기 함수이기 때문에
        // 블락을 안 해주면 stream 함수 영향으로
        // 아래 테스트 함수가 저장함수가 끝나기 전에 실행될 수 있다.
        movieInfoRepository.saveAll(movieInfos)
            .blockLast()
    }

    @AfterEach
    fun finish() {
        movieInfoRepository.deleteAll()
            .block()
    }
    @Test
    fun findAll(){
        //given
        //when
        val moviesInfoFlux: Flux<MovieInfo> = movieInfoRepository.findAll()
        //then
        // 반응형 레포지토리 검증기
        StepVerifier.create(moviesInfoFlux)
            .expectNextCount(3)
            .verifyComplete()
    }


    @Test
    fun findById(){
        //given
        //when
        val movieInfo: Mono<MovieInfo> = movieInfoRepository.findById("c")
        //then
        StepVerifier.create(movieInfo)
            .assertNext{
                movieInfoMono
                -> assertEquals("다크나이트 라이즈",movieInfoMono.name)
            }
            .verifyComplete()
    }

    @Test
    fun saveMovieInfo(){
        //given
        //when
        val saved: Mono<MovieInfo> = movieInfoRepository.save(
            MovieInfo.of(
                "d",
                "배트맨 비긴즈1",
                2005,
                mutableListOf("C크리스찬 베일", "마이클 케인"),
                LocalDate.parse("2005-06-15")
            )
        ).log()
        //then
        StepVerifier.create(saved)
            .assertNext {
                movieInfo
                -> assertNotNull(movieInfo.movieInfoId)
                    assertEquals(movieInfo.name,"배트맨 비긴즈1")
            }
            .verifyComplete()
    }


    @Test
    fun updateMovieInfo(){
        //given
        // block을 해야 스트림이 풀려서 mono타입이 해제가 된다.
        val movieInfoMono:MovieInfo = movieInfoRepository.findById("c").block()!!
        movieInfoMono.year = 2021
        //when
        val save:Mono<MovieInfo> = movieInfoRepository.save(movieInfoMono)
        //then
        StepVerifier.create(save)
            .assertNext { movieInfo ->
                assertNotNull(movieInfo.movieInfoId)
                assertEquals(2021, movieInfo.year)
            }
            .verifyComplete()

    }

    @Test
    fun deleteMovieInfo() {
        //given
        //when
        // block 처리를 해야 함수가 끝이 난다.
        movieInfoRepository.deleteById("c").block()
        val movieInfoFlux = movieInfoRepository.findAll().log()
        //then
        StepVerifier.create(movieInfoFlux)
            .expectNextCount(2)
            .verifyComplete()
    }

    @Test
    fun findByYear() {
        //given
        val year = 2005
        //when
        val moviesInfoFlux = movieInfoRepository.findByYear(year).log()
        //then
        StepVerifier.create(moviesInfoFlux)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun findByName() {
        //given
        val name = "다크나이트 라이즈"
        //when
        val moviesInfoMono = movieInfoRepository.findByName(name).log()
        //then
        StepVerifier.create(moviesInfoMono)
            .assertNext{ movieInfo ->
                assertNotNull(movieInfo.movieInfoId)
                assertEquals("다크나이트 라이즈", movieInfo.name)
            }
            .verifyComplete()
    }
}