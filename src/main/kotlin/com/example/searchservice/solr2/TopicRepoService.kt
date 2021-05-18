package com.example.searchservice.solr2

import com.example.searchservice.core.models.Topic

import org.springframework.stereotype.Service
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.client.solrj.impl.XMLResponseParser
import org.apache.solr.client.solrj.SolrQuery

@Service
class TopicRepoService : BaseRepository<Topic> {
    private val url = "http://localhost:8983/solr/topic"
    private val solrClient: HttpSolrClient = HttpSolrClient.Builder(url).build()

    constructor() {
        solrClient.setParser(XMLResponseParser())
    }

    override fun bulkInsert(entities: Array<Map<String,Any>>): Unit {
        val list = ArrayList<Topic>()
        for (entity in entities) {
            list.add(Topic(entity["id"] as Int, entity["title"] as String, entity["text"] as String))
        }
        solrClient.addBeans(list)
        solrClient.commit()
        println("Done! Inserted ${list.size} documents!")
    }

    override fun insert(entity: Map<*,*>): Unit {
        solrClient.addBean(Topic(entity["id"] as Int, entity["title"] as String, entity["text"] as String))
        solrClient.commit()
        println("Done!")
    }

    override fun search(searchTerm: String): List<Any> {
        val query = SolrQuery()
        val q = if (Regex("^[a-zA-Z0-9\\s]*").matches(searchTerm)) createSearchCriteria(searchTerm) else searchTerm // Excludes special characters
        query.setQuery(q)
        query.setStart(0)
        query.setRows(10000) // will fetch up to 10000 entries
        val response = solrClient.query(query)
        return response.getResults().toList()
    }

    override fun getAll(): List<Any> {
        return search("*:*") // This queries all documents in this repo
    }

    private fun createSearchCriteria(query: String): String {
        var resultQuery = ""
        val words = query.split(" ")
        for (field in arrayOf("id", "title", "text")) {
            resultQuery += "$field:"
            for (word in words) {
                resultQuery += " *$word* "
            }
        }
        return resultQuery
    }
}