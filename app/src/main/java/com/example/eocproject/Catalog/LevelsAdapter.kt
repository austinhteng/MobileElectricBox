package com.example.eocproject.Catalog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.eocproject.Game.DemoActivity
import com.example.eocproject.Game.GameViewModel
import com.example.eocproject.Game.Grid
import com.example.eocproject.Game.PlayGame
import com.example.eocproject.Game.WireGrid
import com.example.eocproject.MainActivity
import com.example.eocproject.databinding.LevelListRowBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File

class LevelsAdapter(private val catalogViewModel: CatalogViewModel, val gameViewModel: GameViewModel,
                    val context: Context) : ListAdapter<LevelMeta, LevelsAdapter.VH>(Diff()) {
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

    private fun getPos(holder: VH) : Int {
        val pos = holder.adapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.adapterPosition
        }
        return pos
    }

    inner class VH(private val levelListRowBinding: LevelListRowBinding) :
        RecyclerView.ViewHolder(levelListRowBinding.root) {
        fun bind(holder: VH, position: Int) {
            holder.levelListRowBinding.levelName.text = catalogViewModel.getLevel(adapterPosition).levelName

            holder.levelListRowBinding.levelName.setOnClickListener {
                val level = catalogViewModel.getLevel(getPos(holder))
                var gridFile = MainActivity.localLevelFile(level.gridUUID)
                var wireFile = MainActivity.localLevelFile(level.wireUUID)
                var invFile = MainActivity.localLevelFile(level.invUUID)
            }
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