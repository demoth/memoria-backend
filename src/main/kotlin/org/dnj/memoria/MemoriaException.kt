package org.dnj.memoria

import org.springframework.http.HttpStatus

open class MemoriaException(message: String, val httpStatus: HttpStatus) : Exception(message)

class ValidationException(message: String) : MemoriaException(message, HttpStatus.BAD_REQUEST)