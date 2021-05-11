package com.example.searchservice.core.repositories.base

interface BaseRepositoryService {
    fun search(searchTerm: String): Array<Any>
}