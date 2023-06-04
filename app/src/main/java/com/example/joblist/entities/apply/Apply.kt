package com.example.joblist.entities.apply

import com.example.joblist.entities.Candidate
import com.example.joblist.entities.Job

data class Apply(
    val applyDate: String,
    val candidateApply: String,
    val id: Long,
    val salary: Double,
    val location: String,
    val jobApply: String,
    val status: String
)
