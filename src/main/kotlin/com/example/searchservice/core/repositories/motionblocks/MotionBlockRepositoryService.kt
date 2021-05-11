package com.example.searchservice.core.repositories.motionblocks

import com.example.searchservice.core.repositories.base.*

import org.springframework.stereotype.Service

import javax.annotation.Resource

@Service
class MotionBlockRepositoryService : BaseRepositoryService {
    @Resource
    private val repository: MotionBlockRepository? = null

    override fun search(searchTerm: String): Array<Any> {
        return emptyArray()
    }
}