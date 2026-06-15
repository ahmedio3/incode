package com.incode.app.data.model

data class ServerConfig(
    val host: String = "127.0.0.1",
    val port: Int = 4096,
    val username: String = "",
    val password: String = ""
) {
    val baseUrl: String
        get() = "http://$host:$port"

    val isValid: Boolean
        get() = host.isNotBlank() && port in 1..65535
}
