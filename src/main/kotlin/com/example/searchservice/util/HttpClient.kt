package com.example.searchservice.util

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.web.client.RestTemplate

import java.net.URI

class HttpClient(private val serverUrl: String) {
    private val http: RestTemplate = RestTemplate()
    private val httpHeaders: HttpHeaders = HttpHeaders()
    // private val http: WebClient = WebClient().builder().baseUrl(serverUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON) // currently not supported

    init {
        httpHeaders.add("Content-Type", "application/json")
        httpHeaders.add("Accept", "application/json")
    }

    fun get(path: String): String? {
        return send(path, HttpMethod.GET)
    }

    fun post(path: String, json: String = ""): String? {
        return send(path, HttpMethod.POST, json)
    }

    private fun send(path: String, method: HttpMethod, json: String = ""): String? {
        val request = HttpEntity(json, httpHeaders)
        val url = serverUrl + path
        return http.exchange(url, method, request, String::class.java).getBody()
    }
}