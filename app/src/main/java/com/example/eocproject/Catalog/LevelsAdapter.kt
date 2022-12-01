package com.example.eocproject.Catalog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.eocproject.databinding.LevelListRowBinding

class LevelsAdapter(private val catalogViewModel: CatalogViewModel) : ListAdapter<LevelMeta, LevelsAdapter.VH>(Diff()) {
    class Diff : DiffUtil.ItemCallback<LevelMeta>() {
        override fun areItemsTheSame(oldItem: LevelMeta, newItem: LevelMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
        }

        override fun areContentsTheSame(oldItem: LevelMeta, newItem: LevelMeta): Boolean {
            return oldItem.firestoreID == newItem.firestoreID
                    && oldItem.levelName == newItem.levelName
                    && oldItem.timeStamp == newItem.timeStamp
                    && oldItem.gridUUID == newItem.gridUUID
                    && oldItem.wireUUID == newItem.wireUUID
                    && oldItem.invUUID == newItem.invUUID
        }
    }


    inner class VH(private val levelListRowBinding: LevelListRowBinding) :
        RecyclerView.ViewHolder(levelListRowBinding.root) {
        fun bind(holder: VH, position: Int) {
            val level = catalogViewModel.getLevel(position)
            holder.levelListRowBinding.levelName.text = level.levelName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val levelListRowBinding = LevelListRowBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(levelListRowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(holder, position)
    }
}