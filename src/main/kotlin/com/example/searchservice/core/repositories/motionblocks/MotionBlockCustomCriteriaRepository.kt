package com.example.searchservice.core.repositories.motionblocks

interface MotionBlockCustomCriteriaRepository {
    fun search(searchTerm: String): List<Any>
}