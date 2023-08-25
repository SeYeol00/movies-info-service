package com.info.moviesinfoservice.domain

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import lombok.Data
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Data
@Document // 몽고디비랑 매핑하는 엔티티 어노테이션
data class MovieInfo(

    @Id
    val movieInfoId:String,
    @NotBlank(message = "영화 제목은 반드시 포함 되어야 합니다.")
    val name:String,
    @Positive(message = "개봉 연도는 양수여야 합니다.")
    val year:Int,
    val cast:MutableList<@NotBlank(message = "출연 배우는 제공되어야 합니다.") String>,
    val releaseDate:LocalDate
) {

    companion object{
        fun of(
            movieInfoId: String,
            name: String,
            year: Int,
            cast: MutableList<String>,
            releaseDate: LocalDate
        ): MovieInfo{
            return MovieInfo(
                movieInfoId,
                name,
                year,
                cast,
                releaseDate
            )
        }
    }
}