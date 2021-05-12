package com.example.searchservice.core.models

import org.springframework.data.solr.core.mapping.SolrDocument
import org.springframework.data.solr.core.mapping.Indexed
import org.springframework.data.annotation.Id

import org.apache.solr.client.solrj.beans.Field

import lombok.NoArgsConstructor
import lombok.AllArgsConstructor
import lombok.Data

@Data
@AllArgsConstructor
@NoArgsConstructor
@SolrDocument(collection = "motion_block")
data class MotionBlock(
    @Id
    // @Indexed(name = "id", type = "int")
    @Field("id")
    val id: Int,

    // @Indexed(name = "title", type = "string")
    @Field("title")
    val title: String,

    @Field("internal")
    // @Indexed(name = "internal", type = "boolean")
    val internal: Boolean
) {
    companion object {
        val COLLECTION = "motion_block"
    }
}