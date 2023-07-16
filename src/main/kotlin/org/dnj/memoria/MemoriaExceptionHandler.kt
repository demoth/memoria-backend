package org.dnj.memoria

import org.dnj.memoria.model.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class MemoriaExceptionHandler {
    private val logger = LoggerFactory.getLogger(MemoriaExceptionHandler::class.java)
    @ExceptionHandler(MemoriaException::class)
    fun handleAllExceptions(e: MemoriaException): ResponseEntity<ErrorResponse> {
        if (e.httpStatus.is5xxServerError)
            logger.error("Request exception:", e)
        
        return ResponseEntity(ErrorResponse(e.message!!), e.httpStatus)
    }
}