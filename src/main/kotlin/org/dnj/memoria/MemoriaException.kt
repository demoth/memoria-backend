package org.dnj.memoria

import org.springframework.http.HttpStatus

class MemoriaException(override val message: String, val httpStatus: HttpStatus) : Exception(message)

class ValidationException(override val message: String) : Exception(message)