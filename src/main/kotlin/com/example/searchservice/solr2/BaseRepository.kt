package com.example.searchservice.solr2

interface BaseRepository<T> {
    fun insert(entity: Map<*, *>): Unit
    fun search(searchTerm: String): List<Any>
    fun getAll(): List<Any>
}