package org.dnj.memoria

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TaskController {
    @GetMapping("/test")
    fun getTask() : ResponseEntity<String> {
        return ResponseEntity("Ok", HttpStatusCode.valueOf(200))
    }
}