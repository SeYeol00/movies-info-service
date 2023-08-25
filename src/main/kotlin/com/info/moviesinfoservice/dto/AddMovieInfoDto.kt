package com.info.moviesinfoservice.dto;

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Add
import java.time.LocalDate


data class AddMovieInfoDto (

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
        ):AddMovieInfoDto{
            return AddMovieInfoDto(
                movieInfoId, name, year, cast, releaseDate
            )
        }
    }

}
