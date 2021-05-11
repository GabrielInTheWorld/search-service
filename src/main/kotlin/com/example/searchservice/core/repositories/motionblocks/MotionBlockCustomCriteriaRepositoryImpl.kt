package com.example.searchservice.core.repositories.motionblocks

import com.example.searchservice.core.repositories.base.*

import com.example.searchservice.core.models.*

import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Repository

import javax.annotation.Resource

@Repository
class MotionBlockCustomCriteriaRepositoryImpl : MotionBlockCustomCriteriaRepository {
    @Resource
    private val solrTemplate: SolrTemplate? = null

    override fun search(searchTerm: String): List<Any> {
        val words: List<String> = searchTerm.split(" ")
        
        val conditions: Criteria = createSearchConditions(words)
        val searchQuery: SimpleQuery = SimpleQuery(conditions)
        val results = solrTemplate!!.queryForPage("motion_block", searchQuery, MotionBlock::class.java)

        return results.getContent()
    }

    private fun createSearchConditions(words: List<String>): Criteria {
        var conditions: Criteria = createCriteria(words[0])
        for (i in 1..words.size) {
            conditions = conditions.or(createCriteria(words[i]))
        }
        return conditions
    }

    private fun createCriteria(word: String): Criteria {
        return Criteria("title").contains(word).or(Criteria("internal").contains(word)).or(Criteria("id").contains(word))
    }
}

// @Repository
// class MotionBlockCustomCriteriaRepository : BaseCustomCriteriaRepositoryImpl(MotionBlock::class.java, "motion_block") {
//     protected override fun createCriteria(word: String): Criteria {
//         return Criteria("title").contains(word).or(Criteria("internal").contains(word)).or(Criteria("id").contains(word))
//     }
// }