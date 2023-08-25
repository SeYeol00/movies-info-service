package com.info.moviesinfoservice.dto

import com.info.moviesinfoservice.domain.MovieInfo
import java.time.LocalDate


data class GetMovieInfoDto(

    private val movieInfoId:String,

    private val name:String,

    private val year:Int,
    private val cast:MutableList<String>,
    private val releaseDate: LocalDate
){
    companion object{
        fun of(
            movieInfo: MovieInfo
        ):GetMovieInfoDto{
            return GetMovieInfoDto(
                movieInfo.movieInfoId,
                movieInfo.name,
                movieInfo.year,
                movieInfo.cast,
                movieInfo.releaseDate
            )
        }
    }
}
