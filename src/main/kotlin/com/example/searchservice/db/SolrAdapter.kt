package com.example.searchservice.db

import com.example.searchservice.db.IAdapter

import org.springframework.beans.factory.annotation.Autowired

import org.apache.solr.client.solrj.impl.HttpSolrClient
// import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.XMLResponseParser
import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrInputDocument
import org.apache.solr.common.SolrDocumentList

// Reference https://solr.apache.org/docs/7_1_0/solr-solrj/
// Reference https://solr.apache.org/guide/7_1/using-solrj.html
// Reference https://solr.apache.org/guide/8_8/getting-started.html

class SolrAdapter: IAdapter {
    @Autowired
    private val solrClient: HttpSolrClient? = null
    
    init {
        println("solrClient: $solrClient")
    }

    override fun bulkInsert(collection: String, entities: Array<Map<String, Any>>): Unit {
        println("Inserting entities in $collection")
        for (entity in entities) {
            insert(collection, entity)
        }
    }

    override fun insert(collection: String, entity: Map<*, *>): Unit {
        val id = entity["id"] as Int
        val document: SolrInputDocument = SolrInputDocument()
        for ((key, value) in entity) {
            try {
                document.addField(key as String, value)
            } catch (e: Exception) {
                println("An error occurred for key $key:$value\n" + e.message)
            }
        }
        document.addField("collection", collection)
        document.setField("id", "$collection/$id")
        try {
            solrClient?.add(document)
            solrClient?.commit()
            println("Done!")
        } catch (e: Exception) {
            println("An error occurred: " + e.message)
        }
    }

    override fun search(searchQuery: String): List<Any> {
        println("Search query $searchQuery")
        // var sq = searchQuery.replace("&&", "AND")
        // sq = sq.replace("||", "OR")
        val query: SolrQuery = SolrQuery()
        query.setQuery(searchQuery)
        query.setStart(0)
        query.setRows(100)

        val response: QueryResponse = solrClient!!.query(query)
        val list: List<Any> = response.getResults().toList()
        println("${list.size} results.")
        return list
    }

    private fun getSolrClient(): HttpSolrClient {
        val url: String = "http://localhost:8983/solr/gettingstarted"
        val client: HttpSolrClient = HttpSolrClient.Builder(url).build()
        client.setParser(XMLResponseParser())
        return client
    }
}