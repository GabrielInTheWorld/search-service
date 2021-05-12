package com.example.searchservice.api;

import com.example.searchservice.db.ArangoAdapter;
import com.example.searchservice.db.SolrAdapter

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource
import lombok.AllArgsConstructor

@RestController
@AllArgsConstructor
class ApiController(val solrDb: SolrAdapter, val arangoDb: ArangoAdapter) {
    var id = 0

    // @Resource
    // private val arangoDb: ArangoAdapter? = null

    // @Resource
    // private val solrDb: SolrAdapter? = null


    // val arangoDb = ArangoAdapter("Arango")
    // val solrDb = SolrAdapter()

    @GetMapping("/greeting")
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name: String): Greeting {
        return Greeting(++id, name)
    }

    @PostMapping("/presenter:arango.update", consumes=["application/json"], produces=["application/json"])
    fun presenter(@RequestBody json: Map<String, Array<Map<String, Any>>>): Any {
        for ((key, value) in json) {
            arangoDb?.bulkInsert(key, value)
        }
        return json
    }

    @PostMapping("/presenter:arango.search", consumes=["application/json"], produces=["application/json"])
    fun presenterArangoSearch(@RequestBody json: Array<Presenter>): Any {
        val presenter = json[0]
        val result = arangoDb!!.search(presenter.data.search_query)
        return result
    }

    @PostMapping("/presenter:solr.update", consumes=["application/json"], produces=["application/json"])
    fun presenterSolr(@RequestBody json: Map<String, Array<Map<String, Any>>>): Any {
        for ((key, value) in json) {
            solrDb?.bulkInsert(key, value)
        }
        return json
    }

    @PostMapping("/presenter:solr.search", consumes=["application/json"], produces=["application/json"])
    fun presenterSolrSearch(@RequestBody json: Array<Presenter>): Any {
        val presenter: Presenter = json[0]
        val result = solrDb!!.search(presenter.data.search_query)
        return result
    }

    @PostMapping("/presenter:solr.get_all", consumes=["application/json"], produces=["application/json"])
    fun presenterSolrGetAll(): Any {
        return solrDb.getAll()
    }
}

data class Greeting(val id: Int, val name: String)
data class Presenter(val presenter: String, val data: PresenterData)
data class PresenterData(val search_query: String)