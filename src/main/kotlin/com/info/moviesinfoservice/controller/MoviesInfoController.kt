package com.info.moviesinfoservice.controller

import com.info.moviesinfoservice.domain.MovieInfo
import com.info.moviesinfoservice.dto.AddMovieInfoDto
import com.info.moviesinfoservice.dto.GetMovieInfoDto
import com.info.moviesinfoservice.dto.UpdateMovieInfoDto
import com.info.moviesinfoservice.service.MoviesInfoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks


@RestController
@RequestMapping("/v1")
class MoviesInfoController(
    private val moviesInfoService: MoviesInfoService,
) {
    val moviesInfoSink: Sinks.Many<MovieInfo> = Sinks.many().replay().all()


    @GetMapping("/movieinfos")
    fun getAllMovieInfos(@RequestParam(value = "year", required = false)year:Int?): Flux<GetMovieInfoDto> {
        if (year != null){
            return moviesInfoService.getMovieInfoByYear(year).log()
        }
        return moviesInfoService.getAllMovieInfos().log()
    }

    @PutMapping("/movieinfos/{id}")
    fun updateMovieInfo(@PathVariable id:String,@RequestBody updateMovieInfoDto: UpdateMovieInfoDto)
    : Mono<ResponseEntity<GetMovieInfoDto>> {
        return moviesInfoService.updateMovieInfo(id,updateMovieInfoDto)
            .map {
                movieInfo
                -> ResponseEntity.ok().body(movieInfo)
            }
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
            .log()
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMovieInfo(@PathVariable id:String)
    :Mono<Void>{
        return moviesInfoService.deleteMovieInfo(id).log()
    }

    @GetMapping("/movieinfos/{id}")
    fun getMovieInfoById(@PathVariable id:String)
    :Mono<ResponseEntity<GetMovieInfoDto>>{
        return moviesInfoService.getMovieInfoById(id)
            .map{
                movieInfo
                -> ResponseEntity.ok().body(movieInfo)
            }
            .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
            .log()
    }

    // SSE
    @GetMapping(value = ["/movieinfos/stream"], produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getMovieInfoById():Flux<MovieInfo>{
        // 실시간으로 post로 온 영화 정보를 Sink 클래스 인스턴스에 저장한 값을 계속 업데이트해서 받음
        return moviesInfoSink.asFlux().log()
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    fun addMovieInfo(@RequestBody @Valid addMovieInfoDto: AddMovieInfoDto)
    :Mono<MovieInfo>{
        return moviesInfoService.addMovieInfo(addMovieInfoDto)
            // Sink에 전달해서 SSE api에 대기
            .doOnNext{
                saved
                ->
                    moviesInfoSink.tryEmitNext(saved)
                }
            .log()
            }

    }
    // publish that movie to something
    // Sink 클래스를 쓸 것임
    // -> Subscriber와 Publisher 둘 다 가능하여 SSE를 지원한다.
    // Subscriber to this movie info
