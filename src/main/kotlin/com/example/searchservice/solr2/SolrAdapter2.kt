package com.example.searchservice.solr2

import com.example.searchservice.core.models.*
import com.example.searchservice.db.IAdapter

import javax.annotation.Resource
import org.springframework.stereotype.Service

@Service
class SolrAdapter2(@Resource val topicRepo: TopicRepoService, @Resource val motionblockRepo: MotionBlockRepoService) : IAdapter {

    private val repoMap = mapOf(
        Topic.COLLECTION to topicRepo,
        MotionBlock.COLLECTION to motionblockRepo
    )

    override fun bulkInsert(collection: String, entities: Array<Map<String, Any>>): Unit {
        for (entity in entities) {
            insert(collection, entity)
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
        for ((key, value) in repoMap) {
            list.addAll(value.search(searchQuery))
        }
        return list
    }

    override fun getAll(): List<Any> {
        val list = ArrayList<Any>()
        for ((key, value) in repoMap) {
            list.addAll(value.getAll())
        }
        return list
    }
}