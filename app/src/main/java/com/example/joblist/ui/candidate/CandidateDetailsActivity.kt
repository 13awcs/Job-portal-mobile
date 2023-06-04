package com.example.joblist.ui.candidate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.joblist.R
import com.example.joblist.databinding.ActivityCandidateDetailsBinding
import com.example.joblist.entities.Candidate
import com.example.joblist.utils.Constants

class CandidateDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCandidateDetailsBinding
    private var candidate: Candidate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCandidateDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    private fun setView() {
        try {
            candidate = intent.getSerializableExtra(Constants.EXTRAS_CANDIDATE) as Candidate

            if (candidate != null) {
                Glide
                    .with(this)
                    .load(candidate!!.avatar)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(binding.ivAvatar)
                binding.etName.setText(candidate!!.name)
                binding.etEmail.setText(candidate!!.email)
                binding.etPhone.setText(candidate!!.phone)
                binding.etAddress.setText(candidate!!.address)
                binding.etCategory.setText(candidate!!.category)
            }

            Log.e("TAG", "setView: $candidate", )
        } catch (e: Exception) {
            Log.e("TAG", "setView: $e", )
        }
    }
}
