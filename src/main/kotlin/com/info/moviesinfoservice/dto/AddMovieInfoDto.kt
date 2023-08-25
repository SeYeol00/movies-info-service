package com.info.moviesinfoservice.dto;

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators.Add
import java.time.LocalDate


data class AddMovieInfoDto (

    var movieInfoId:String,
    @NotBlank(message = "영화 제목은 반드시 포함 되어야 합니다.")
    var name:String,
    @Positive(message = "개봉 연도는 양수여야 합니다.")
    var year:Int,
    var cast:MutableList<@NotBlank(message = "출연 배우는 제공되어야 합니다.") String>,
    var releaseDate:LocalDate
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
