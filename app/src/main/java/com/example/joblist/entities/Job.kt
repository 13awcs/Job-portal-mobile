package com.example.joblist.entities

import java.io.Serializable

data class Job(
    val id: Long = 0,
    val amount: Int = 0,
    val apply: Int = 0,
    val category: String = "",
    val companyName: String = "",
    val createAt: String = "",
    val deadline: String = "",
    val description: String = "",
    val level: String,
    val location: String,
    val salary: Double,
    val status: String = "",
    val title: String,
    val type: String = "",
    val recruiterId: Long,
) : Serializable
