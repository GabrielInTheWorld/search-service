package com.example.searchservice.core.models

import org.springframework.data.solr.core.mapping.SolrDocument
import org.springframework.data.solr.core.mapping.Indexed
import org.springframework.data.annotation.Id

@SolrDocument(solrCoreName = "motion_block")
data class MotionBlock(
    @Id
    @Indexed(name = "id", type = "int")
    val id: Int,

    @Indexed(name = "title", type = "string")
    val title: String,

    @Indexed(name = "internal", type = "boolean")
    val internal: Boolean
)