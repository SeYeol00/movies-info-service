package com.info.moviesinfoservice.intg.controller

import com.info.moviesinfoservice.controller.MoviesInfoController
import com.info.moviesinfoservice.domain.MovieInfo
import com.info.moviesinfoservice.domain.MovieInfoRepository
import com.info.moviesinfoservice.dto.AddMovieInfoDto
import com.info.moviesinfoservice.dto.GetMovieInfoDto
import com.info.moviesinfoservice.dto.UpdateMovieInfoDto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.net.URI
import java.time.LocalDate

// 테스트를 돌릴 때 랜덤으로 포트를 띄워서 배포 중인 서버와 포트가 겹치지 않게 하는 어노테이션
// 통합 테스트 -> 스프링부트테스트 어노테이션
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient // WebTestClient 활성화
class MovieInfoControllerIntgTest {

    @Autowired
    private lateinit var movieInfoRepository: MovieInfoRepository

    @Autowired
    private lateinit var webTestClient: WebTestClient

    companion object{
        const val MOVIES_INFO_URL = "/v1/movieinfos"
    }

    @BeforeEach
    fun init(){
        val movieInfos: List<MovieInfo> = listOf(
            MovieInfo(
                "a",
                "배트맨 비긴즈",
                2005,
                mutableListOf("크리스찬 베일", "마이클 케인"),
                LocalDate.parse("2005-06-15")
            ),
            MovieInfo(
                "b",
                "다크나이트",
                2008,
                mutableListOf("크리스찬 베일", "히스 레저"),
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
            .log()
            .blockLast()
    }

    @Test
    fun addMovieInfo(){
        //given
        val addMovieInfo: AddMovieInfoDto = AddMovieInfoDto.of(
            "a",
            "배트맨 비긴즈",
            2005,
            mutableListOf("크리스찬 베일", "마이클 케인"),
            LocalDate.parse("2005-06-15")
        )
        //given
        webTestClient
            .post()
            .uri(MOVIES_INFO_URL)
            // post일 때 리퀘스트로 보내는 바디 벨류
            // 리퀘스트 바디 정의
            .bodyValue(addMovieInfo)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(GetMovieInfoDto::class.java)
            .consumeWith{
                movieInfoEntityExchangeResult
                -> val responseBody: GetMovieInfoDto = movieInfoEntityExchangeResult.responseBody!!
                assert(responseBody!=null)
                assert(responseBody.movieInfoId=="a")
            }
    }


    @Test
    fun getAllMovieInfos(){
        //when
        webTestClient
            .get()
            .uri(MOVIES_INFO_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful

            .expectBodyList(GetMovieInfoDto::class.java)
            .hasSize(3)
    }


    @Test
    fun getAllMoviesInfos_stream(){
        //given
        val addMovieInfo: AddMovieInfoDto = AddMovieInfoDto.of(
            "a",
            "배트맨 비긴즈",
            2005,
            mutableListOf("크리스찬 베일", "마이클 케인"),
            LocalDate.parse("2005-06-15")
        )
        //when
        webTestClient
            .post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(addMovieInfo)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(GetMovieInfoDto::class.java)
            .consumeWith {
                movieInfoEntity
                -> val responseBody: GetMovieInfoDto? = movieInfoEntity.responseBody
                // 검증 로직
                assert(responseBody!=null)
                assert(responseBody!!.movieInfoId !=null)
            }

        val movieInfoFlux: Flux<MovieInfo> = webTestClient
            .get()
            .uri(MOVIES_INFO_URL + "/stream")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .returnResult(MovieInfo::class.java)
            .responseBody

        StepVerifier.create(movieInfoFlux)
            .assertNext{movieInfo1 ->
                assert(movieInfo1.movieInfoId != null)
            }
            .thenCancel()
            .verify()
    }


    @Test
    fun getMovieInfoByYear(){
        val uri: URI = UriComponentsBuilder.fromUriString(MOVIES_INFO_URL)
            .queryParam("year", 2005)
            .buildAndExpand().toUri()

        //when
        webTestClient
            .get()
            .uri(uri)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList(GetMovieInfoDto::class.java)
            .hasSize(1)
    }

    @Test
    fun getMovieInfoId(){
        //given
        val movieInfoId:String = "c"
        //when
        webTestClient
            .get()
            .uri(
                "$MOVIES_INFO_URL/$movieInfoId"
            )
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.name").isEqualTo("다크나이트 라이즈")
    }


    @Test
    fun updateMovieInfo(){
        //given
        val updateMovieInfoDto: UpdateMovieInfoDto = UpdateMovieInfoDto(
            "c",
            "다크나이트 라이즈 업데이트",
            2012,
            mutableListOf("크리스찬 베일", "톰 하디"),
            LocalDate.parse("2012-07-20")
        )
        val movieInfoId:String = "c"
        //when
        webTestClient
            .put()
            .uri("$MOVIES_INFO_URL/$movieInfoId")
            .bodyValue(updateMovieInfoDto)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(GetMovieInfoDto::class.java)
            .consumeWith {
                movieInfo
                -> val responseBody: GetMovieInfoDto? = movieInfo.responseBody
                assert(responseBody!!.name == "다크나이트 라이즈 업데이트")
            }

    }

    @Test
    fun deleteMovieInfo(){
        //given
        val movieInfoId:String = "a"
        //when
        webTestClient
            .delete()
            .uri("$MOVIES_INFO_URL/$movieInfoId")
            .exchange()
            .expectStatus()
            .isNoContent
            .expectBody(GetMovieInfoDto::class.java)
            .consumeWith {
                movieEntityExchangeResult
                -> val responseBody: GetMovieInfoDto? = movieEntityExchangeResult.responseBody
                assertNull(responseBody)
            }
    }

    @Test
    fun updatedMovieInfo_notfound(){
        //given
        val updateMovieInfoDto: UpdateMovieInfoDto = UpdateMovieInfoDto(
            "a",
            "배트맨 비긴즈",
            2005,
            mutableListOf("크리스찬 베일", "마이클 케인"),
            LocalDate.parse("2005-06-15")
        )
        val movieInfoId = "z"
        //when
        webTestClient
            .put()
            .uri("$MOVIES_INFO_URL/$movieInfoId")
            .bodyValue(updateMovieInfoDto)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun getMovieInfoById_notFound(){
        //given
        val movieInfo:String = "z"
        //when
        webTestClient
            .get()
            .uri("$MOVIES_INFO_URL/$movieInfo")
            .exchange()
            .expectStatus()
            .isNotFound
    }


}