package com.example.joblist.api.services

import com.example.joblist.entities.*
import com.example.joblist.entities.apply.ApplyResponse
import retrofit2.Call
import retrofit2.http.*

interface  AppService {
    @GET("candidate/apply/applies/newest")
    fun getAllApplies(): Call<ApplyResponse>

    @POST("/recruit/recruiter/login")
    fun login(@Body login: Login): Call<UserResponse>

    @POST("/register")
    fun register(@Body register: Register): Call<UserResponse>

    @GET("/recruit/recruiter/profile")
    fun getProfile(@Query("username") username: String): Call<User>

    @POST("/recruit/recruiter/profile/edit")
    fun editProfile(@Query("id") id: Long, @Body newUser: User): Call<User>

    @GET("/candidate/candidates")
    fun getAllCandidate(): Call<List<Candidate>>

    @GET("/candidate/candidate/{id}")
    fun getCandidateById(@Path("id") id: Long): Call<Candidate>

//    @GET("/candidates/search")
//    fun searchCandidate(@Query("keyword") keyword: String?): Call<List<Candidate?>?>?

    @GET("/recruit/job/jobs/recruiter/{id}")
    fun getAllJobs(@Path("id") recruiterId: Long): Call<List<Job>>

    @GET("/recruit/job/{id}")
    fun getJobById(@Path("id") id: Long): Call<Job?>

    @POST("/jobs/create")
    fun createJobs(@Body job: Job): Call<CreatedJobResponse>

    @PUT("recruit/job/edit/{id}")
    fun editJobs(@Path("id") id: Long, @Body newJob: Job): Call<EditedJobResponse>

    @DELETE("recruit/job/delete/{id}")
    fun deleteJobs(@Path("id") id: Long): Call<EditedJobResponse>
}
