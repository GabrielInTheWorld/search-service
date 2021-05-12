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

    override fun insert(entity: Map<*,*>): Unit {
        solrClient.addBean(Topic(entity["id"] as Int, entity["title"] as String, entity["text"] as String))
        solrClient.commit()
        println("Done!")
    }

    override fun search(searchTerm: String): List<Any> {
        val query = SolrQuery()
        query.setQuery(searchTerm)
        query.setStart(0)
        query.setRows(100) // will fetch 100 entries
        val response = solrClient.query(query)
        return response.getResults().toList()
    }

    override fun getAll(): List<Any> {
        return search("*:*") // This queries all documents in this repo
    }
}