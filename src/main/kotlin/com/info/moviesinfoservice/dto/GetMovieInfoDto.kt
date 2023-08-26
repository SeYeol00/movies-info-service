package com.info.moviesinfoservice.dto

import com.info.moviesinfoservice.domain.MovieInfo
import java.time.LocalDate


data class GetMovieInfoDto(

    val movieInfoId:String,

    val name:String,

    val year:Int,
    val cast:MutableList<String>,
    val releaseDate: LocalDate
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
