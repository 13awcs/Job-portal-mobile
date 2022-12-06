package com.example.joblist.ui.main.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.joblist.R
import com.example.joblist.base.BaseFragment
import com.example.joblist.databinding.FragmentJobBinding
import com.example.joblist.databinding.LayoutCreateJobDialogBinding
import com.example.joblist.entities.Job
import com.example.joblist.ui.job.JobDetailsActivity
import com.example.joblist.ui.main.MainViewModel
import com.example.joblist.ui.main.adapters.JobAdapter
import com.example.joblist.utils.Constants
import com.example.joblist.utils.Resource
import com.example.joblist.utils.SharedPreferencesUtils
import kotlinx.coroutines.launch

class JobFragment : BaseFragment() {
    private lateinit var binding: FragmentJobBinding
    private lateinit var bindingDialog: LayoutCreateJobDialogBinding
    private val activityViewModel: MainViewModel by viewModels()
    private lateinit var jobAdapter: JobAdapter
    private lateinit var pd: ProgressDialog
    private lateinit var dialog: AlertDialog

    override fun onSearch(query: String?) {
        super.onSearch(query)
        jobAdapter.filter.filter(query)
    }

    override fun onCreateJob() {
        super.onCreateJob()
        dialog.show()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.itCreateJob)?.isVisible = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobBinding.inflate(inflater, container, false)
        bindingDialog = LayoutCreateJobDialogBinding.inflate(layoutInflater)
        dialog = AlertDialog.Builder(requireContext())
            .setView(bindingDialog.root)
            .create()

        setView()
        setObserver()
        setListener()
        return binding.root
    }

    private fun setView() {
        pd = ProgressDialog(requireContext()).apply {
            setMessage("Loading")
        }

        jobAdapter = JobAdapter().apply {
            binding.rvJobs.adapter = this
            binding.rvJobs.setHasFixedSize(true)
            binding.rvJobs.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    RecyclerView.VERTICAL,
                )
            )
        }
        activityViewModel.getAllJobs(
            SharedPreferencesUtils.loadString(
                requireContext(),
                Constants.KEY_UID
            ).toLong()
        )
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    activityViewModel.jobsState.collect {
                        when (it) {
                            is Resource.Loading -> {
                                if (it.isLoading) pd.show() else pd.hide()
                            }
                            is Resource.Success -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                                jobAdapter.submitData(it.data?.toMutableList())
                            }
                            is Resource.Error -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                            }
                        }
                    }
                }
                launch {
                    activityViewModel.jobState.collect {
                        when (it) {
                            is Resource.Loading -> {
                                if (it.isLoading) pd.show() else pd.hide()
                            }
                            is Resource.Success -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                                startActivity(
                                    Intent(
                                        requireContext(),
                                        JobDetailsActivity::class.java
                                    ).apply {
                                        putExtra(Constants.EXTRAS_JOB, it.data)
                                    })
                            }
                            is Resource.Error -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                            }
                        }
                    }
                }
                launch {
                    activityViewModel.createJobState.collect {
                        when (it) {
                            is Resource.Loading -> {
                                if (it.isLoading) pd.show() else pd.hide()
                            }
                            is Resource.Success -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                                activityViewModel.getAllJobs(
                                    SharedPreferencesUtils.loadString(
                                        requireContext(),
                                        Constants.KEY_UID
                                    ).toLong())
                            }
                            is Resource.Error -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                            }
                        }
                    }
                }
                launch {
                    activityViewModel.editJobState.collect {
                        when (it) {
                            is Resource.Loading -> {
                                if (it.isLoading) pd.show() else pd.hide()
                            }
                            is Resource.Success -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                                activityViewModel.getAllJobs(
                                    SharedPreferencesUtils.loadString(
                                        requireContext(),
                                        Constants.KEY_UID
                                    ).toLong())
                            }
                            is Resource.Error -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                            }
                        }
                    }
                }
                launch {
                    activityViewModel.deleteJobState.collect {
                        when (it) {
                            is Resource.Loading -> {
                                if (it.isLoading) pd.show() else pd.hide()
                            }
                            is Resource.Success -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                                activityViewModel.getAllJobs(
                                    SharedPreferencesUtils.loadString(
                                        requireContext(),
                                        Constants.KEY_UID
                                    ).toLong())
                            }
                            is Resource.Error -> {
                                it.message?.let { msg -> if (msg.isNotEmpty()) toast(msg) }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setListener() {
        bindingDialog.btCancel.setOnClickListener {
            clearDialog()
            jobAdapter.notifyDataSetChanged()
        }

        bindingDialog.btOK.setOnClickListener {
            modifyJob(bindingDialog.tvId.text.isNullOrEmpty())
            clearDialog()
        }

        jobAdapter.listener = object : JobAdapter.Listener {
            override fun onClick(position: Int) {
                activityViewModel.getJobById(jobAdapter.currentList[position].id)
            }

            override fun onRightSwipe(position: Int) {
                with(jobAdapter.currentList[position]) {
                    bindingDialog.tvId.text = id.toString()
                    bindingDialog.etTitle.setText(title)
                    bindingDialog.etLevel.setText(level)
                    bindingDialog.etAddress.setText(location)
                    bindingDialog.etSalary.setText(salary.toString())
                }
                dialog.show()
            }

            override fun onLeftSwipe(position: Int) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete this item?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        activityViewModel.deleteJob(jobAdapter.currentList[position].id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        jobAdapter.notifyItemChanged(position)
                        dialog.dismiss()
                    }
                    .create().show()
            }
        }
    }

    private fun clearDialog() {
        bindingDialog.tvId.text = ""
        bindingDialog.etTitle.setText("")
        bindingDialog.etLevel.setText("")
        bindingDialog.etAddress.setText("")
        bindingDialog.etSalary.setText("")

        dialog.dismiss()
    }

    private fun modifyJob(isNew: Boolean) {
        Log.d("DATLT", "modifyJob: $isNew")
        if (isNew) {
            //todo add new Job
            activityViewModel.createJob(
                Job(
                    title = bindingDialog.etTitle.text.toString(),
                    level = bindingDialog.etLevel.text.toString(),
                    location = bindingDialog.etAddress.text.toString(),
                    salary = bindingDialog.etSalary.text.toString().toDouble(),
                    recruiterId = SharedPreferencesUtils.loadString(
                        requireContext(),
                        Constants.KEY_UID
                    ).toLong()
                ),
            )
        } else {
            // todo edit exiting Job
            activityViewModel.editJob(
                bindingDialog.tvId.text.toString().toLong(),
                Job(
                    title = bindingDialog.etTitle.text.toString(),
                    level = bindingDialog.etLevel.text.toString(),
                    location = bindingDialog.etAddress.text.toString(),
                    salary = bindingDialog.etSalary.text.toString().toDouble(),
                    recruiterId = SharedPreferencesUtils.loadString(
                        requireContext(),
                        Constants.KEY_UID
                    ).toLong()
                ),
            )
        }
    }
}
