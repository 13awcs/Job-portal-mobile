package com.example.joblist.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.joblist.databinding.RowApplyBinding
import com.example.joblist.entities.apply.Apply

class ApplyAdapter : ListAdapter<Apply, RecyclerView.ViewHolder>(DiffCallback()) {
    private lateinit var binding: RowApplyBinding
    var listener: Listener? = null
    private var applies = mutableListOf<Apply>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = RowApplyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).displayData(getItem(position))
    }

    fun submitData(applies: MutableList<Apply>?) {
        this.applies = applies!!
        notifyDataSetChanged()
        submitList(applies)
    }

    class ViewHolder(
        private val binding: RowApplyBinding,
        private val listener: Listener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun displayData(apply: Apply) {
            binding.tvJobName.text = apply.title
            binding.tvSalary.text = apply.salary.toString()
            binding.tvAddress.text = apply.location
            binding.tvCandidateName.text = "Applied by ${apply.name}"
            itemView.setOnClickListener {
                listener?.onClick(adapterPosition)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Apply>() {
        override fun areItemsTheSame(oldItem: Apply, newItem: Apply): Boolean =
            oldItem === newItem

        override fun areContentsTheSame(oldItem: Apply, newItem: Apply): Boolean =
            oldItem.id == newItem.id
    }

    interface Listener {
        fun onClick(position: Int)
    }
}
