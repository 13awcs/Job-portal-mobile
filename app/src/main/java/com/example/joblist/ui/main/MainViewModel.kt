package com.example.joblist.ui.main

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joblist.api.AppRetrofit
import com.example.joblist.api.services.AppService
import com.example.joblist.entities.*
import com.example.joblist.entities.apply.ApplyResponse
import com.example.joblist.ui.main.database.AppDatabase
import com.example.joblist.utils.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.awaitResponse
import java.util.*

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

    private val _applyState = MutableStateFlow<Resource<ApplyResponse>>(Resource.Loading(false))
    val applyState = _applyState.asStateFlow()

    private val _profileState = MutableStateFlow<Resource<User>>(Resource.Loading(false))
    val profileState = _profileState.asStateFlow()

    private val _editProfileState = MutableStateFlow<Resource<User>>(Resource.Loading(false))
    val editProfileState = _editProfileState.asStateFlow()

    private val _candidatesState = MutableStateFlow<Resource<List<Candidate>>>(Resource.Loading(false))
    val candidatesState = _candidatesState.asStateFlow()

    private val _candidateState = MutableStateFlow<Resource<Candidate>>(Resource.Loading(false))
    val candidateState = _candidateState.asStateFlow()

    private val _jobsState = MutableStateFlow<Resource<List<com.example.joblist.entities.Job>>>(Resource.Loading(false))
    val jobsState = _jobsState.asStateFlow()

    private val _jobState = MutableStateFlow<Resource<com.example.joblist.entities.Job>>(Resource.Loading(false))
    val jobState = _jobState.asStateFlow()

    private val _createJobState = MutableStateFlow<Resource<CreatedJobResponse>>(Resource.Loading(false))
    val createJobState = _createJobState.asStateFlow()

    private val _editJobState = MutableStateFlow<Resource<EditedJobResponse>>(Resource.Loading(false))
    val editJobState = _editJobState.asStateFlow()

    private val _deleteJobState = MutableStateFlow<Resource<EditedJobResponse>>(Resource.Loading(false))
    val deleteJobState = _deleteJobState.asStateFlow()

    private val _getAllNewsState = MutableStateFlow<Resource<MutableList<News>>>(Resource.Loading(false))
    val getAllNewsState = _getAllNewsState.asStateFlow()

    private val _deleteNewsState = MutableStateFlow<Resource<String>>(Resource.Loading(false))
    val deleteNewsState = _deleteNewsState.asStateFlow()

    private val _createNewsState = MutableStateFlow<Resource<String>>(Resource.Loading(false))
    val createNewsState = _createNewsState.asStateFlow()

    fun getAllNews(context: Context) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _getAllNewsState.emit(Resource.Loading(true))
            withContext(Dispatchers.IO) {
                try {
                    _getAllNewsState.emit(Resource.Success(
                        data = AppDatabase.getInstance(context)!!.newsDao().getAll(),
                        message= "Successfully")
                    )
                } catch (e:Exception) {
                    _getAllNewsState.emit(Resource.Error(e.message!!))
                }
            }
        }.also {
            it.invokeOnCompletion {
                viewModelScope.launch {
                    _getAllNewsState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun deleteNews(context: Context, news: News) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _deleteNewsState.emit(Resource.Loading(true))
            withContext(Dispatchers.IO) {
                try {
                    AppDatabase.getInstance(context)!!.newsDao().delete(news)
                    getAllNews(context)
                    _deleteNewsState.emit(Resource.Success(null, message= "Done"))
                } catch (e:Exception) {
                    _deleteNewsState.emit(Resource.Error(e.message!!))
                }
            }
        }.also {
            it.invokeOnCompletion {
                viewModelScope.launch {
                    _deleteNewsState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun createNews(context: Context, title: String, content: String) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _createNewsState.emit(Resource.Loading(true))
            withContext(Dispatchers.IO) {
                try {
                    AppDatabase.getInstance(context)!!.newsDao().insert(
                        News(null, title, content, Date().toString())
                    )
                    getAllNews(context)
                    _createNewsState.emit(Resource.Success(null, message= "Done"))
                } catch (e:Exception) {
                    _createNewsState.emit(Resource.Error(e.message!!))
                }
            }
        }.also {
            it.invokeOnCompletion {
                viewModelScope.launch {
                    _createNewsState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun getAllApplies(id: Long) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _applyState.emit(Resource.Loading(true))
            withContext(Dispatchers.IO) {
                val response = appService.getAllApplies(id).awaitResponse()
                _applyState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _applyState.emit(Resource.Loading(false))
                }
            }
        }
    }

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

    fun editProfile(id: Long, newUser: User) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _editProfileState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.editProfile(id, newUser).awaitResponse()
                _editProfileState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _editProfileState.emit(Resource.Loading(false))
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

    fun getCandidateById(id: Long) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _candidateState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.getCandidateById(id).awaitResponse()
                _candidateState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _candidateState.emit(Resource.Loading(false))
                }
            }
        }
    }

    fun getAllJobs(recruiterId: Long) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _jobsState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.getAllJobs(recruiterId).awaitResponse()
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

    fun getJobById(id: Long) {
        job = viewModelScope.launch(coroutineExceptionHandler) {
            _jobState.emit(Resource.Loading(true))

            withContext(Dispatchers.IO) {
                val response = appService.getJobById(id).awaitResponse()
                _jobState.emit(
                    if (response.isSuccessful) Resource.Success(
                        response.body(), response.message()
                    )
                    else Resource.Error(response.errorBody().toString())
                )
            }
        }.apply {
            invokeOnCompletion {
                viewModelScope.launch {
                    _jobState.emit(Resource.Loading(false))
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
