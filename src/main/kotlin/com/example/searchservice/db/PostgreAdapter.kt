package com.example.searchservice.db

import org.springframework.stereotype.Service

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.searchservice.util.*

@Service
class PostgreAdapter : IAdapter {
    val url: String = "jdbc:postgresql://localhost:5432/openslides"
    val user: String = "openslides"
    val password: String = "openslides"

    val http = HttpClient("http://localhost:9011/internal/datastore/writer")

    var connection = initConnection()

    init {
        
    }

    override fun bulkInsert(collection: String, entities: Array<Map<String, Any>>): Unit {
        for (entity in entities) {
            insert(collection, entity)
        }
    }

    override fun insert(collection: String, entity: Map<*, *>): Unit {
        val id: Int = entity["id"] as Int
        var document = "\"fields\": {"
        for ((key, value) in entity) {
            document += "\"${key}\":  \"${value}\", "
            // when (value) {
            //     value is Int -> document += " ${value}, "
            //     value is Boolean -> document += " ${value}, "
            //     value is String -> document += " \"${value}\", "
            // }
        }
        document += "}"
        val json = "{\"user_id\": 1, \"locked_fields\": {}, \"information\": {}, \"events\": [{\"type\": \"create\", \"fqid\": \"$collection/$id\", $document}]}"
        http.post("/write", json)
    }
    
    override fun search(searchQuery: String): List<Any> {
        return emptyList()
    }

    override fun getAll(): List<Any> {
        val statement = connection.createStatement()
        val result = statement.executeQuery("SELECT fqid from models;")
        if (result.next()) {
            // println(result.)
        //    println(result.getString(0))
        //    println(result.getString(1)) 
        }
        // var i: Int = 0
        // while (result.next()) {
        //     println(result.getString(i))
        //     ++i
        // }
        return emptyList()
        // return result.
    }

    // private fun getTypeof(value: Any): String {
    //     if (value.to) {
    //         return "INTEGER"
    //     } else if (value == true || value == false) {
    //         return "BOOLEAN"
    //     } else {
    //         return "STRING"
    //     }
    // }

    private fun initConnection(): Connection {
        try {
            val connection: Connection = DriverManager.getConnection(url, user, password)
            val statement: Statement = connection.createStatement()
            val result: ResultSet = statement.executeQuery("SELECT VERSION()")
            if (result.next()) {
                println("Connected with postgreSQL! Version: ${result.getString(1)}")
            }
            result.close()
            statement.close()
            return connection
        } catch (e: Exception) {
            println("An error occurred while connecting to postgreSQL:\n$e")
            throw Exception("PostgreSQL is not available...")
        }
    }

    fun String.toInt(radix: Int = 10): Int? = toIntOrNull(radix)
}