package org.dnj.memoria

import java.util.Date

data class User(val name: String, val password: String, val creationDate: Date = Date())