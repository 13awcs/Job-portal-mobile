package com.example.joblist.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joblist.api.AppRetrofit
import com.example.joblist.api.services.AppService
import com.example.joblist.entities.Candidate
import com.example.joblist.entities.CreatedJobResponse
import com.example.joblist.entities.EditedJobResponse
import com.example.joblist.entities.User
import com.example.joblist.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.awaitResponse

class MainViewModel : ViewModel() { 
    private val appService: AppService by lazy {
        AppRetrofit.instance.create(AppService::class.java)
    }
    private lateinit var job: Job
    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.message?.let {
                viewModelScope.launch {
                    _profileState.emit(Resource.Error(throwable.toString()))
                    _candidatesState.emit(Resource.Error(throwable.toString()))
                    _jobsState.emit(Resource.Error(throwable.toString()))
                    _createJobState.emit(Resource.Error(throwable.toString()))
                    _editJobState.emit(Resource.Error(throwable.toString()))
                    _deleteJobState.emit(Resource.Error(throwable.toString()))
                    Log.e("TAG", "Error: $it")
                }
            }
        }

    private val _profileState = MutableStateFlow<Resource<User>>(Resource.Loading(false))
    val profileState = _profileState.asStateFlow()

    private val _candidatesState = MutableStateFlow<Resource<List<Candidate>>>(Resource.Loading(false))
    val candidatesState = _candidatesState.asStateFlow()

    private val _jobsState = MutableStateFlow<Resource<List<com.example.joblist.entities.Job>>>(Resource.Loading(false))
    val jobsState = _jobsState.asStateFlow()

    private val _createJobState = MutableStateFlow<Resource<CreatedJobResponse>>(Resource.Loading(false))
    val createJobState = _createJobState.asStateFlow()

    private val _editJobState = MutableStateFlow<Resource<EditedJobResponse>>(Resource.Loading(false))
    val editJobState = _editJobState.asStateFlow()

    private val _deleteJobState = MutableStateFlow<Resource<EditedJobResponse>>(Resource.Loading(false))
    val deleteJobState = _deleteJobState.asStateFlow()

    fun getProfile(username: String) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _profileState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.getProfile(username).awaitResponse()
                _profileState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _profileState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun getAllCandidates() {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _candidatesState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.getAllCandidate().awaitResponse()
                _candidatesState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _candidatesState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun getAllJobs() {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _jobsState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.getAllJobs().awaitResponse()
                _jobsState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _jobsState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun createJob(theJob: com.example.joblist.entities.Job) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _createJobState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.createJobs(theJob).awaitResponse()
                _createJobState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _createJobState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun editJob(id: Long, newJob: com.example.joblist.entities.Job) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _editJobState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.editJobs(id, newJob).awaitResponse()
                _editJobState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _editJobState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun deleteJob(id: Long) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _deleteJobState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.deleteJobs(id).awaitResponse()
                _deleteJobState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _deleteJobState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun cancel() {
        if (job.isActive) job.cancel(CancellationException("Stop working"))
        viewModelScope.launch {
            _profileState.emit(Resource.Error("Cancelled"))
            _candidatesState.emit(Resource.Error("Cancelled"))
            _jobsState.emit(Resource.Error("Cancelled"))
            _createJobState.emit(Resource.Error("Cancelled"))
            _editJobState.emit(Resource.Error("Cancelled"))
            _deleteJobState.emit(Resource.Error("Cancelled"))
        }
    }
}