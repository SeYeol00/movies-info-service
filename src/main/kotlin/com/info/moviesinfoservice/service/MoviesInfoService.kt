package com.info.moviesinfoservice.service

import com.info.moviesinfoservice.domain.MovieInfo
import com.info.moviesinfoservice.dto.AddMovieInfoDto
import com.info.moviesinfoservice.dto.GetMovieInfoDto
import com.info.moviesinfoservice.dto.UpdateMovieInfoDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface MoviesInfoService {
    suspend fun addMovieInfo(addMovieInfo: AddMovieInfoDto): Mono<MovieInfo>
    suspend fun getAllMovieInfos():Flux<GetMovieInfoDto>
    suspend fun getMovieInfoById(id:String):Mono<GetMovieInfoDto>
    suspend fun updateMovieInfo(id:String, updateMovieInfoDto: UpdateMovieInfoDto): Mono<GetMovieInfoDto>
    suspend fun deleteMovieInfo(id: String):Mono<Void>
    suspend fun getMovieInfoByYear(year:Int):Flux<GetMovieInfoDto>
}