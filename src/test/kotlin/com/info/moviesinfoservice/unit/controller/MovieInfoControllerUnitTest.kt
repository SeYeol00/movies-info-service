package com.info.moviesinfoservice.unit.controller

import com.info.moviesinfoservice.controller.MoviesInfoController
import com.info.moviesinfoservice.dto.GetMovieInfoDto
import com.info.moviesinfoservice.service.MoviesInfoService
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import java.time.LocalDate

@WebFluxTest(controllers = [MoviesInfoController::class])
@AutoConfigureWebTestClient
class MovieInfoControllerUnitTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    // 모키토 프레임워크가 아닌 스프링 프레임워크 어노테이션
    // 스프링 어플리케이션 컨텍스트 내에서 빈의 모의 객체를 생성하는데 사용된다.
    // 일반적으로 통합 테스트에서 실제 빈을 모의 버전으로 대체하는데 사용된다.
    @MockBean
    private lateinit var moviesInfoService: MoviesInfoService

//    private val moviesInfoServiceMock = mockk<MoviesInfoService>()

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
        `when`(moviesInfoService.getAllMovieInfos()) // list를 파라미터로 보내면 Flux로 변환
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
        //
    }




}
