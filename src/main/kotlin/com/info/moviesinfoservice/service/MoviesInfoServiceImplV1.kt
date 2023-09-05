package com.info.moviesinfoservice.service

import com.info.moviesinfoservice.domain.MovieInfo
import com.info.moviesinfoservice.domain.MovieInfoRepository
import com.info.moviesinfoservice.dto.AddMovieInfoDto
import com.info.moviesinfoservice.dto.GetMovieInfoDto
import com.info.moviesinfoservice.dto.UpdateMovieInfoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MoviesInfoServiceImplV1(
    private val movieInfoRepository: MovieInfoRepository
):MoviesInfoService {
    override suspend fun addMovieInfo(addMovieInfo: AddMovieInfoDto)
    : Mono<MovieInfo> {
        return movieInfoRepository.save(
            MovieInfo.of(
                addMovieInfo.movieInfoId,
                addMovieInfo.name,
                addMovieInfo.year,
                addMovieInfo.cast,
                addMovieInfo.releaseDate
            )
        )
            .log()
    }

    override suspend fun getAllMovieInfos(): Flux<GetMovieInfoDto> {
        return movieInfoRepository.findAll()
            .map {
                movieInfo
                -> GetMovieInfoDto
                    .of(movieInfo)
            }
            .log()
    }

    override suspend fun getMovieInfoById(id: String): Mono<GetMovieInfoDto> {
        return movieInfoRepository.findById(id)
            .map {
                movieInfo
                -> GetMovieInfoDto
                    .of(movieInfo)
            }
            .log()
    }

    override suspend fun updateMovieInfo(id: String, updateMovieInfoDto: UpdateMovieInfoDto): Mono<GetMovieInfoDto> {
        return movieInfoRepository.findById(id)
            // 업데이트는 flatmap을 많이 쓴다.
            .flatMap<GetMovieInfoDto?> {
                movieInfo
                -> movieInfo.update(updateMovieInfoDto)
                movieInfoRepository.save(movieInfo)
                    .map {
                        updated
                        -> GetMovieInfoDto
                            .of(updated)
                    }
            }
            .log()
    }

    override suspend fun deleteMovieInfo(id: String): Mono<Void> {
        return movieInfoRepository.deleteById(id).log()
    }

    override suspend fun getMovieInfoByYear(year: Int): Flux<GetMovieInfoDto> {
        return movieInfoRepository.findByYear(year)
            .map {
                movieInfo
                -> GetMovieInfoDto
                    .of(movieInfo)
            }
    }
}