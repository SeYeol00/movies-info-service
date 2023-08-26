package com.info.moviesinfoservice.intg.controller

import com.info.moviesinfoservice.domain.MovieInfoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

// 테스트를 돌릴 때 랜덤으로 포트를 띄워서 배포 중인 서버와 포트가 겹치지 않게 하는 어노테이션
// 통합 테스트 -> 스프링부트테스트 어노테이션
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient // WebTestClient 활성화
class MovieInfoController {

    @Autowired
    private lateinit var movieInfoRepository: MovieInfoRepository

    @Autowired
    private lateinit var webTestClient: WebTestClient

    companion object{
        const val MOVIES_INFO
    }
}