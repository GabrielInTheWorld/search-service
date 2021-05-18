package com.example.searchservice.solr2

import com.example.searchservice.core.models.*
import com.example.searchservice.db.IAdapter

import javax.annotation.Resource
import org.springframework.stereotype.Service
import kotlin.system.measureTimeMillis

@Service
class SolrAdapter2(@Resource val topicRepo: TopicRepoService, @Resource val motionblockRepo: MotionBlockRepoService) : IAdapter {

    private val repoMap = mapOf(
        Topic.COLLECTION to topicRepo,
        MotionBlock.COLLECTION to motionblockRepo
    )

    override fun bulkInsert(collection: String, entities: Array<Map<String, Any>>): Unit {
        if (repoMap.containsKey(collection)) {
            repoMap[collection]?.bulkInsert(entities) 
        } else {
            println("Unknown collection: $collection")
        }
    }

    override fun insert(collection: String, entity: Map<*, *>): Unit {
        if (repoMap.containsKey(collection)) {
            repoMap[collection]?.insert(entity)
        } else {
            println("Unknown collection: $collection")
        }
    }

    override fun search(searchQuery: String): List<Any> {
        val list = ArrayList<Any>()
        val elapsed = measureTimeMillis { 
            for ((key, value) in repoMap) {
                list.addAll(value.search(searchQuery))
            }
        }
        println("Search result after ${elapsed} ms.")
        return list
    }

    override fun getAll(): List<Any> {
        val list = ArrayList<Any>()
        val elapsed = measureTimeMillis {
            for ((key, value) in repoMap) {
                list.addAll(value.getAll())
            }
        }
        println("Get_All result after ${elapsed} ms.")
        return list
    }
}