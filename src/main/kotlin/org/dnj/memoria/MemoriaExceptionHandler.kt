package org.dnj.memoria

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class MemoriaExceptionHandler {
    @ExceptionHandler(MemoriaException::class)
    fun handleAllExceptions(e: MemoriaException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(e.message), e.httpStatus)
    }
}