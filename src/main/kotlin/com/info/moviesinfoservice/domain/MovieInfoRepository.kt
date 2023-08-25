package com.info.moviesinfoservice.domain

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Year

@Repository
interface MovieInfoRepository: ReactiveMongoRepository<MovieInfo,String> {
    fun findByYear(year: Int): Flux<MovieInfo>

    fun findByName(name:String): Mono<MovieInfo>
}