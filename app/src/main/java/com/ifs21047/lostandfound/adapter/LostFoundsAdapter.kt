package com.ifs21047.lostfounds.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ifs21047.delcomtodo.data.remote.response.LostFoundsItemResponse
import com.ifs21047.lostandfound.databinding.ItemRowLostfoundBinding

class LostFoundsAdapter :
    ListAdapter<LostFoundsItemResponse,
            LostFoundsAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback
    private var originalData = mutableListOf<LostFoundsItemResponse>()
    private var filteredData = mutableListOf<LostFoundsItemResponse>()

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowLostfoundBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = originalData[originalData.indexOf(getItem(position))]

        holder.binding.cbItemLostFoundIsFinished.setOnCheckedChangeListener(null)
        holder.binding.cbItemLostFoundIsFinished.setOnLongClickListener(null)

        holder.bind(data)

        holder.binding.cbItemLostFoundIsFinished.setOnCheckedChangeListener { _, isChecked ->
            data.isCompleted = if (isChecked) 1 else 0
            holder.bind(data)
            onItemClickCallback.onCheckedChangeListener(data, isChecked)
        }

        holder.binding.ivItemLostFoundDetail.setOnClickListener {
            onItemClickCallback.onClickDetailListener(data.id)
        }
    }

    class MyViewHolder(val binding: ItemRowLostfoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: LostFoundsItemResponse) {
            binding.apply {
                tvItemLostFoundTitle.text = data.title
                cbItemLostFoundIsFinished.isChecked = data.isCompleted == 1
                val statusText = if (data.status.equals("found", ignoreCase = true)) {
                    // Jika status "found", maka gunakan warna hijau
                    highlightText("Found", Color.GREEN)
                } else {
                    // Jika status "lost", maka gunakan warna kuning
                    highlightText("Lost", Color.YELLOW)
                }
                // Menetapkan teaks status yang sudah disorot ke TextView
                tvLostFoundDetailStatus.text = statusText
            }
        }

        private fun highlightText(text: String, color: Int): SpannableString {
            val spannableString = SpannableString(text)
            val foregroundColorSpan = ForegroundColorSpan(color)
            spannableString.setSpan(foregroundColorSpan, 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableString
        }
    }

    fun submitOriginalList(list: List<LostFoundsItemResponse>) {
        originalData = list.toMutableList()
        filteredData = list.toMutableList()

        submitList(originalData)
    }

    fun filter(query: String) {
        filteredData = if (query.isEmpty()) {
            originalData
        } else {
            originalData.filter {
                (it.title.contains(query, ignoreCase = true))
            }.toMutableList()
        }

        submitList(filteredData)
    }

    interface OnItemClickCallback {
        fun onCheckedChangeListener(todo: LostFoundsItemResponse, isChecked: Boolean)
        fun onClickDetailListener(todoId: Int)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LostFoundsItemResponse>() {
            override fun areItemsTheSame(
                oldItem: LostFoundsItemResponse,
                newItem: LostFoundsItemResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: LostFoundsItemResponse,
                newItem: LostFoundsItemResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
