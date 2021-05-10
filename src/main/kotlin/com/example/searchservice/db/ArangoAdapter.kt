package com.example.searchservice.db

import com.example.searchservice.db.IAdapter
import com.example.searchservice.util.HttpClient

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.ArangoDBException;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.ViewEntity
import com.arangodb.entity.ViewType

import com.google.gson.Gson

// import org.json.JSONObject

// Reference: http://arangodb.github.io/arangodb-java-driver/javadoc-6_12/

class ArangoAdapter(val dbName: String): IAdapter {
    private val http = HttpClient("http://localhost:8529/_db/$dbName")

    private val arangodb: ArangoDB = ArangoDB.Builder().host("127.0.0.1", 8529).build()
    private val collections: ArrayList<String> = ArrayList()
    private var collectionViews: ViewEntity? = null
    private val collectionViewsName: String = "collectionViews"

    private val allFields: ArrayList<String> = ArrayList()

    init {
        createDb()
        createView()

    }

    override fun bulkInsert(collection: String, entities: Array<Map<String, Any>>): Unit {
        for (entity in entities) {
            insert(collection, entity)
        }
    }

    override fun insert(collection: String, entity: Map<*, *>): Unit {
        val arangoCollection = createCollection(collection)
        val document = BaseDocument()
        document.setKey((entity["id"] as Int).toString())
        for ((key, value) in entity) {
            if (!allFields.contains(key as String)) {
                allFields.add(key)
            }
            document.addAttribute(key as String, value)
        }
        arangoCollection.insertDocument(document)
        println("Done!")
    }

    override fun search(searchQuery: String): Array<Any> {
        return emptyArray()
    }

    private fun createDb(): Unit {
        if (arangodb.db(dbName).exists()) {
            arangodb.db(dbName).drop()
        }
        arangodb.createDatabase(dbName)
        println("Created database: $dbName")
    }

    private fun createView(): Unit {
        collectionViews = arangodb.db(dbName).createView(collectionViewsName, ViewType.ARANGO_SEARCH)
    }

    private fun createCollection(collection: String): ArangoCollection {
        val arangoCollection = arangodb.db(dbName).collection(collection)
        if (!arangoCollection.exists()) {
            collections.add(collection)
            arangoCollection.create()
            linkCollectionToView(collection)
            println("Created collection: $collection")
        }
        return arangoCollection
    }

    private fun linkCollectionToView(collection: String): Unit {
        val json: String = "{\"links\": {\"$collection\": {\"includeAllFields\": true}}}"
        http.patch("/_api/view/$collectionViewsName/properties#arangosearch", json)
    }
}