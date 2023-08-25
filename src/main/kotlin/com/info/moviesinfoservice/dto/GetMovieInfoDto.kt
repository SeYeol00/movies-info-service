package com.info.moviesinfoservice.dto

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
            movieInfoId:String,
            name:String,
            year:Int,
            cast:MutableList<String>,
            releaseDate: LocalDate
        ):GetMovieInfoDto{
            return GetMovieInfoDto(
                movieInfoId,
                name,
                year,
                cast,
                releaseDate
            )
        }
    }
}
