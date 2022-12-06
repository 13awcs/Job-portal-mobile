package com.example.joblist.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.joblist.databinding.RowNewsBinding
import com.example.joblist.entities.News

class NewsAdapter : ListAdapter<News, RecyclerView.ViewHolder>(DiffCallback()) {
    private lateinit var binding: RowNewsBinding
    var listener: Listener? = null
    private var newsList = mutableListOf<News>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = RowNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).displayData(getItem(position))
    }

    fun submitData(newsList: MutableList<News>?) {
        this.newsList = newsList!!
        notifyDataSetChanged()
        submitList(newsList)
    }

    class ViewHolder(
        private val binding: RowNewsBinding,
        private val listener: Listener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun displayData(news: News) {
            binding.tvTitle.text = news.title
            binding.tvContent.text = news.content
            binding.tvCreatedDate.text = news.createdDate
            itemView.setOnClickListener {
                listener?.onClick(adapterPosition)
            }
            itemView.setOnLongClickListener{
                listener?.onLongClick(adapterPosition)
                true
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
            oldItem.id == newItem.id
    }

    interface Listener {
        fun onClick(position: Int)
        fun onLongClick(position: Int)
    }
}
