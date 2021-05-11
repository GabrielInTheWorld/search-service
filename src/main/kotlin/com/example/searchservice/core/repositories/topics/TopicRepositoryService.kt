package com.example.searchservice.core.repositories.topics

import com.example.searchservice.core.repositories.base.*

import org.springframework.stereotype.Service

import javax.annotation.Resource

@Service
class TopicRepositoryService : BaseRepositoryService {
    @Resource
    private val repository: TopicRepository? = null

    override fun search(searchTerm: String): Array<Any> {
        return emptyArray()
    }
}