package com.example.searchservice.core.repositories.topics

interface TopicCustomCriteriaRepository {
    fun search(searchTerm: String): List<Any>
}