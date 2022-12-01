package com.example.eocproject.Catalog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eocproject.databinding.CatalogFragBinding

class CatalogFrag() : Fragment() {
    companion object {
        val catalogFragTag = "catalogFragTag"
    }

    val catalogViewModel: CatalogViewModel by viewModels()
    lateinit var binding: CatalogFragBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        binding = CatalogFragBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Long press to edit.
        val adapter = LevelsAdapter(catalogViewModel)

        val rv = binding.levelsListRV
        val itemDecor = DividerItemDecoration(rv.context, LinearLayoutManager.VERTICAL)
        binding.levelsListRV.addItemDecoration(itemDecor)
        binding.levelsListRV.adapter = adapter

        catalogViewModel.observeLevels().observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }

        catalogViewModel.initialFetch()
    }
}