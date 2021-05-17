package com.example.searchservice.db

import org.springframework.stereotype.Service

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
class PostgreAdapter : IAdapter {
    val url: String = "jdbc:postgresql://localhost:5432/openslides"
    val user: String = "openslides"
    val password: String = "openslides"

    init {
        try {
            val connection: Connection = DriverManager.getConnection(url, user, password)
            val statement: Statement = connection.createStatement()
            val result: ResultSet = statement.executeQuery("SELECT VERSION()")
            if (result.next()) {
                println("Connected with postgreSQL! Version: ${result.getString(1)}")
            }
        } catch (e: Exception) {
            println("An error occurred while connecting to postgreSQL:\n$e")
        }
    }

    override fun bulkInsert(collection: String, entities: Array<Map<String, Any>>): Unit {}
    override fun insert(collection: String, entity: Map<*, *>): Unit {}
    override fun search(searchQuery: String): List<Any> {return emptyList()}
    override fun getAll(): List<Any> {return emptyList()}
}