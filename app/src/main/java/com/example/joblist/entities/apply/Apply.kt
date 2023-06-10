package com.example.joblist.entities.apply

data class Apply(
    val applyDate: String,
    val name: String,
    val id: Long,
    val salary: Float,
    val location: String,
    val title: String,
    val status: String
)
