package com.example.joblist.ui.news

import android.os.Bundle
import com.example.joblist.base.BaseActivity
import com.example.joblist.databinding.ActivityNewsDetailsBinding
import com.example.joblist.entities.News
import com.example.joblist.utils.Constants

class NewsDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityNewsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setView()
    }

    private fun setView() {
        val news = intent.getParcelableExtra<News>(Constants.EXTRAS_NEWS)
        binding.tvTitle.text = news?.title
        binding.tvContent.text = news?.content
        binding.tvCreatedDate.text = news?.createdDate
    }
}
