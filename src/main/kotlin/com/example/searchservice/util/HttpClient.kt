package com.example.searchservice.util

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.RequestEntity
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

import java.net.URI

class HttpClient(private val serverUrl: String) {
    private val http: RestTemplate = RestTemplate()
    private val httpHeaders: HttpHeaders = HttpHeaders()
    // private val http: WebClient = WebClient().builder().baseUrl(serverUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON) // currently not supported

    init {
        httpHeaders.add("Content-Type", "application/json")
        httpHeaders.add("Accept", "application/json")

        val requestFactory = HttpComponentsClientHttpRequestFactory()
        requestFactory.setConnectTimeout(500)
        requestFactory.setReadTimeout(500)
        http.setRequestFactory(requestFactory)
    }

    fun get(path: String): String? {
        return send(path, HttpMethod.GET)
    }

    fun post(path: String, json: String = ""): String? {
        return send(path, HttpMethod.POST, json)
    }

    fun patch(path: String, json: String = ""): String? {
        return send(path, HttpMethod.PATCH, json)
    }

    private fun send(path: String, method: HttpMethod, json: String = ""): String? {
        val request = HttpEntity(json, httpHeaders)
        val url = serverUrl + path
        println("Send request to $url with method $method\nData: $json")
        try {
            return http.exchange(url, method, request, String::class.java).getBody()
        } catch (e: Exception) {
            println("Error sending request: $e")
            return e.message
        }
    }
}