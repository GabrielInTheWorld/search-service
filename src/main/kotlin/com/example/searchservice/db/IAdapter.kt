package com.example.searchservice.db

interface IAdapter {
    fun bulkInsert(collection: String, entities: Array<Map<String, Any>>): Unit
    fun insert(collection: String, entity: Map<*, *>): Unit
    fun search(searchQuery: String): List<Any>
    fun getAll(): List<Any>
}