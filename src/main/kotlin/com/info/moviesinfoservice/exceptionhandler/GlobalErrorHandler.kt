package com.info.moviesinfoservice.exceptionhandler

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import java.util.function.Function
import java.util.stream.Collectors


@ControllerAdvice
class GlobalErrorHandler {
    private val log = LoggerFactory.getLogger(GlobalErrorHandler::class.java)

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleRequestBodyError(exception: WebExchangeBindException):ResponseEntity<String>{
        log.error("익셉션이 리퀘스트바디 에러에서 잡혔습니다. : {}", exception.message)
        val error: String = exception.bindingResult.allErrors.stream()
            .map{ obj: ObjectError -> obj.defaultMessage }
            .sorted()
            .collect(Collectors.joining(","))
        log.error("에러는 :{}", error)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }
}