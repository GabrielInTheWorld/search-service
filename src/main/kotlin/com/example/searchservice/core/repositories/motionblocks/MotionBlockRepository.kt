package com.example.searchservice.core.repositories.motionblocks

import com.example.searchservice.core.repositories.base.*
import com.example.searchservice.core.models.MotionBlock

import org.springframework.data.solr.repository.SolrCrudRepository

/**
 * First parameter is the model for which this repository is
 * Second parameter is the type of the id of the models
 */
interface MotionBlockRepository : SolrCrudRepository<MotionBlock, Int> {

}