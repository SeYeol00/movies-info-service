package com.info.moviesinfoservice.unit.controller

import com.info.moviesinfoservice.controller.MoviesInfoController
import com.info.moviesinfoservice.domain.MovieInfo
import com.info.moviesinfoservice.dto.AddMovieInfoDto
import com.info.moviesinfoservice.dto.GetMovieInfoDto
import com.info.moviesinfoservice.dto.UpdateMovieInfoDto
import com.info.moviesinfoservice.service.MoviesInfoService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.isA
import org.mockito.kotlin.isNotNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@WebFluxTest(controllers = [MoviesInfoController::class])
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    // 모키토 프레임워크가 아닌 스프링 프레임워크 어노테이션
    // 스프링 어플리케이션 컨텍스트 내에서 빈의 모의 객체를 생성하는데 사용된다.
    // 일반적으로 통합 테스트에서 실제 빈을 모의 버전으로 대체하는데 사용된다.
//    @MockBean
//    private val moviesInfoServiceMock: MoviesInfoService? = null

//    private val moviesInfoServiceMock = mockk<MoviesInfoService>()

    //코틀린에서 모키토를 쓰려면 모키토 코틀린이 따로 있다!!!!
//    @MockBean
    @MockBean
    private lateinit var moviesInfoService: MoviesInfoService

    companion object{
        val MOVIES_INFO_URL:String = "/v1/movieinfos"
    }


    @Test
    fun getAllMoviesInfo() {
        //given
        val movieInfos: List<GetMovieInfoDto> = listOf(
            GetMovieInfoDto(
                "a",
                "배트맨 비긴즈",
                2005,
                mutableListOf("크리스찬 베일", "마이클 케인"),
                LocalDate.parse("2005-06-15")
            ),
            GetMovieInfoDto(
                "b",
                "다크나이트",
                2008,
                mutableListOf("크리스찬 베일", "히스 레저"),
                LocalDate.parse("2008-07-18")
            ),
            GetMovieInfoDto(
                "c",
                "다크나이트 라이즈",
                2012,
                mutableListOf("크리스찬 베일", "톰 하디"),
                LocalDate.parse("2012-07-20")
            )
        )
        // Mockk => 코틀린용 모키토 라이브러리
//        every {
//            moviesInfoServiceMock.getAllMovieInfos()
//        }
//            .returns(Flux.fromIterable(movieInfos))

        // 모키토 프레임워크 when함수로 Mock으로 주입된 객체의 함수의 동작 지정
        whenever(moviesInfoService.getAllMovieInfos()) // list를 파라미터로 보내면 Flux로 변환
            .thenReturn(Flux.fromIterable(movieInfos))

        //when
        webTestClient
            .get()
            .uri(MOVIES_INFO_URL)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBodyList(
                GetMovieInfoDto::class.java
            )
            .hasSize(3)

    }

    @Test
    fun getMovieInfoById(){
        //given
        val id: String = "c"
        val getMovieInfoDto: GetMovieInfoDto = GetMovieInfoDto(
            "c",
            "다크나이트 라이즈",
            2012,
            mutableListOf("크리스찬 베일", "톰 하디"),
            LocalDate.parse("2012-07-20")
        )

        // mocking and stubbing

        whenever(moviesInfoService.getMovieInfoById(isA()))
            .thenReturn(Mono.just(getMovieInfoDto))

        //when
        webTestClient
            .get()
            .uri("$MOVIES_INFO_URL/$id")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(GetMovieInfoDto::class.java)
            .consumeWith {
                movieInfoEntity
                -> val responseBody: GetMovieInfoDto? = movieInfoEntity.responseBody
                assert( getMovieInfoDto != null )
            }

    }

    @Test
    fun addMovieInfo() {
        //given
        val addMovieInfoDto: AddMovieInfoDto = AddMovieInfoDto(
            "a",
            "배트맨 비긴즈",
            2005,
            mutableListOf("크리스찬 베일", "마이클 케인"),
            LocalDate.parse("2005-06-15")
        )

        //코틀린에서 모키토를 쓰려면 모키토 코틀린이 따로 있다!!!!

        whenever(moviesInfoService.addMovieInfo(
            // 만약 함수가 Non -null 파라미터를 원한다면 anyOrNull()을 써라
            //"any<AddMovieInfoDto>()는 null이 아니어야 합니다."라는 메시지와 함께 NullPointerException이 발생하는 경우
            // 다음 이유 중 하나가 원인일 가능성이 높습니다.
            //
            //Kotlin의 Null 안전성: 모의하려는 메서드는 null이 아닌 매개변수를 기대하지만 내부의 any<T>()는 실제로 null입니다.
            // Java에서는 문제가 없지만 Kotlin에서는 null 안전성 검사로 인해 문제가 발생합니다.
            //잘못된 컨텍스트: 조롱 또는 검증의 컨텍스트에서 any<T>()를 사용하고 있는지 확인하고
            // 실수로 테스트하려는 메서드에 직접 전달하지 않도록 하세요.
            //
            //다음은 몇 가지 해결 방법입니다.
            //
            //1. any() 대신 anyOrNull()을 사용하세요.
            //메서드에 null이 아닌 매개변수가 필요한 경우 Kotlin의 null 안전 검사를 우회해야 하는 anyOrNull() 사용을 고려해 보세요.
            //
            // 2. Mocking에서 인수 유형을 명시적으로 정의합니다.
            //Kotlin의 메서드 서명이 null을 허용하지 않는 유형을 지정하는 경우 해당 유형을 명시적으로 언급해야 합니다.
            isA()
        ))
            .thenReturn(
                Mono.just(MovieInfo
                .of(
                    "a",
                    "배트맨 비긴즈",
                    2005,
                    mutableListOf("크리스찬 베일", "마이클 케인"),
                    LocalDate.parse("2005-06-15")
                )))
        //when
        webTestClient
            .post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(addMovieInfoDto)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody(MovieInfo::class.java)
            .consumeWith {
                movieInfoResult
                -> val responseBody: MovieInfo? = movieInfoResult.responseBody
                assert(responseBody?.movieInfoId != null)
            }


    }

    @Test
    fun updateMovieInfo(){
        //given
        val updateMovieInfoDto: UpdateMovieInfoDto = UpdateMovieInfoDto(
            "c",
            "다크나이트 라이즈1000",
            2012,
            mutableListOf("크리스찬 베일", "톰 하디"),
            LocalDate.parse("2012-07-20")
        )
        val movieInfo:String = "c"

        // mocking and stubbing
        whenever(moviesInfoService.updateMovieInfo(isA(), isA()))
            .thenReturn(Mono.just(
                GetMovieInfoDto(
                    updateMovieInfoDto.movieInfoId,
                    updateMovieInfoDto.name,
                    updateMovieInfoDto.year,
                    updateMovieInfoDto.cast,
                    updateMovieInfoDto.releaseDate
                    )
                )
            )

        //when
        webTestClient
            .put()
            .uri("$MOVIES_INFO_URL/$movieInfo")
            .bodyValue(updateMovieInfoDto)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody(GetMovieInfoDto::class.java)
            .consumeWith {
                movieInfoEntity
                -> val responseBody: GetMovieInfoDto? = movieInfoEntity.responseBody
                assert(responseBody != null)
                assert(responseBody?.name == "다크나이트 라이즈1000")
            }
    }

    @Test
    fun deleteMovieInfo(){
        //given
        val movieInfoId:String = "c"

        // mocking and stubbing
        whenever(moviesInfoService.deleteMovieInfo(isA()))
            .thenReturn(Mono.empty())

        //when
        webTestClient
            .delete()
            .uri("$MOVIES_INFO_URL/$movieInfoId")
            .exchange()
            .expectStatus()
            .isNoContent
            .expectBody(GetMovieInfoDto::class.java)
            .consumeWith {
                movieEntity
                -> val responseBody: GetMovieInfoDto? = movieEntity.responseBody
                assertNull(responseBody)
            }

    }

    @Test
    fun addMovieInfo_validation(){
        //given
        val addMovieInfoDto:AddMovieInfoDto = AddMovieInfoDto(
            "a",
            "배트맨 비긴즈",
            -2005,
            mutableListOf(""),
            LocalDate.parse("2005-06-15")
        )

        //when
        webTestClient
            .post()
            .uri(MOVIES_INFO_URL)
            .bodyValue(addMovieInfoDto)
            .exchange()
            .expectStatus()
            .isBadRequest
            .expectBody(String::class.java)
            .consumeWith {
                exchageResult
                -> val responseBody: String? = exchageResult.responseBody
                println("responseBody : $responseBody")
                val expectedMessage:String = "개봉 연도는 양수여야 합니다."
                assert(responseBody!=null)
                assertEquals(expectedMessage,responseBody)
            }
    }



}
