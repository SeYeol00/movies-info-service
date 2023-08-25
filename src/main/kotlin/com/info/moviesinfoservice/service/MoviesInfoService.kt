package com.info.moviesinfoservice.service

import com.info.moviesinfoservice.domain.MovieInfo
import com.info.moviesinfoservice.dto.AddMovieInfoDto
import com.info.moviesinfoservice.dto.GetMovieInfoDto
import com.info.moviesinfoservice.dto.UpdateMovieInfoDto
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface MoviesInfoService {
    fun addMovieInfo(addMovieInfo: AddMovieInfoDto): Mono<MovieInfo>
    fun getAllMovieInfos():Flux<GetMovieInfoDto>
    fun getMovieInfoById(id:String):Mono<GetMovieInfoDto>
    fun updateMovieInfo(id:String, updateMovieInfoDto: UpdateMovieInfoDto): Mono<GetMovieInfoDto>
    fun deleteMovieInfo(id: String):Mono<Void>
    fun getMovieInfoByYear(year:Int):Flux<GetMovieInfoDto>
}