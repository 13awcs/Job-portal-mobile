package com.example.joblist.ui.job

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.joblist.databinding.ActivityJobDetailsBinding
import com.example.joblist.entities.Job
import com.example.joblist.utils.Constants

class JobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobDetailsBinding
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    private fun setView() {
        try {
            job = intent.getSerializableExtra(Constants.EXTRAS_JOB) as Job

            if (job != null) {
                binding.etCompanyName.setText(job!!.companyName)
                binding.etName.setText(job!!.title)
                binding.etSalary.setText(job!!.salary.toString())
                binding.etAddress.setText(job!!.location)
                binding.etApplied.setText(job!!.apply.toString())
            }

            Log.e("TAG", "setView: $job", )
        } catch (e: Exception) {
            Log.e("TAG", "setView: $e", )
        }
    }
}
