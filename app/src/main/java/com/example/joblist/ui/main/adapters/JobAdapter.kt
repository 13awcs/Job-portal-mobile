package com.example.joblist.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.joblist.databinding.RowJobBinding
import com.example.joblist.entities.Job
import com.example.joblist.utils.SwipeCallback

class JobAdapter : ListAdapter<Job, RecyclerView.ViewHolder>(DiffCallback()), Filterable {
    private lateinit var binding: RowJobBinding
    var listener: Listener? = null
    private var jobs = mutableListOf<Job>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = RowJobBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).displayData(getItem(position))
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var list = mutableListOf<Job>()
                val filterResults = FilterResults()

                if (constraint == null || constraint.toString().isEmpty()) {
                    list = jobs
                } else {
                    jobs.forEach { job ->
                        if (job.title
                                .lowercase()
                                .trim()
                                .contains(constraint.toString().lowercase())
                        ) {
                            list.add(job)
                        }
                    }
                }

                filterResults.values = list
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
                submitList(filterResults?.values as MutableList<Job>)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        ItemTouchHelper(object : SwipeCallback(recyclerView.context) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        listener?.onRightSwipe(viewHolder.adapterPosition)
                    }
                    ItemTouchHelper.LEFT -> {
                        listener?.onLeftSwipe(viewHolder.adapterPosition)
                    }
                }
            }
        }).attachToRecyclerView(recyclerView)
    }

    fun submitData(jobs: MutableList<Job>?) {
        this.jobs = jobs!!
        submitList(jobs)
    }

    class ViewHolder(
        private val binding: RowJobBinding,
        private val listener: Listener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun displayData(job: Job) {
            binding.tvName.text = job.title
            binding.tvSalary.text = "${job.salary}$"
            binding.tvAddress.text = job.location
            binding.tvApplied.text = "Applied: ${job.apply}"
            itemView.setOnClickListener {
                listener?.onClick(adapterPosition)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Job>() {
        override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean =
            oldItem.title == newItem.title && oldItem.salary == newItem.salary && oldItem.level == newItem.level
    }

    interface Listener {
        fun onClick(position: Int)
        fun onRightSwipe(position: Int)
        fun onLeftSwipe(position: Int)
    }
}

