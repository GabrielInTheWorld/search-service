package com.example.searchservice.core.config

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;


@Configuration
@EnableSolrRepositories(
    basePackages = ["com.example.searchservice.core.repositories.motionblocks", "com.example.searchservice.core.repositories.topics"],
    schemaCreationSupport = true,
    // multicoreSupport = true,
    // namedQueriesLocation = "classpath:solr-named-queries.properties"
)
@ComponentScan
class SolrConfig {
    @Bean
    fun solrClient(): SolrClient {
        println("called getClient")
        return HttpSolrClient.Builder("http://localhost:8983/solr").build()
    }

    @Bean
    fun solrTemplate(solrClient: SolrClient): SolrTemplate {
        println("called getTemplate")
        return SolrTemplate(solrClient)
    }
}