package com.example.searchservice.core.repositories.topics

import com.example.searchservice.core.models.Topic

import org.springframework.data.solr.repository.SolrCrudRepository

/**
 * First parameter is the model for which this repository is
 * Second parameter is the type of the id of the models
 */
interface TopicRepository : TopicCustomCriteriaRepository, SolrCrudRepository<Topic, Int> {
    // Here goes (named) queries
}