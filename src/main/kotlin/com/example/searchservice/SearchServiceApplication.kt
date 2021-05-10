package com.example.searchservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// Reference https://docs.spring.io/spring-framework/docs/current/javadoc-api/overview-summary.html

@SpringBootApplication
class SearchServiceApplication

fun main(args: Array<String>) {
	runApplication<SearchServiceApplication>(*args)
}
