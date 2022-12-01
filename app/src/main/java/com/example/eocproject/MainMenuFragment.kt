package com.example.eocproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.eocproject.Catalog.CatalogFrag
import com.example.eocproject.Catalog.CatalogViewModel
import com.example.eocproject.Game.GameViewModel
import com.example.eocproject.Game.PlayGame
import com.example.eocproject.databinding.ContentHomeBinding

class MainMenuFragment: Fragment() {
    companion object {
        fun newInstance(): MainMenuFragment {
            return MainMenuFragment()
        }

        val mainMenuFragTag = "mainMenuFragTag"
    }
    private lateinit var binding : ContentHomeBinding
    private val viewModel : GameViewModel by activityViewModels()
    private val catalogViewModel : CatalogViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ContentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newProjectBut.setOnClickListener {
            viewModel.isCreative = true
            parentFragmentManager.commit {
                val frag = PlayGame.newInstance()
                add(R.id.mainScreen, frag, PlayGame.playFragTag)
                addToBackStack(PlayGame.playFragTag)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                show(frag)
                hide(parentFragmentManager.findFragmentByTag(mainMenuFragTag)!!)
            }
        }

        binding.oldProjectBut.setOnClickListener {
            viewModel.isCreative = false
            parentFragmentManager.commit {
                val frag = CatalogFrag()
                add(R.id.mainScreen, frag, CatalogFrag.catalogFragTag)
                addToBackStack(CatalogFrag.catalogFragTag)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                show(frag)
                hide(parentFragmentManager.findFragmentByTag(mainMenuFragTag)!!)
            }
        }
        Log.d("Main Menu", "view Created")
    }
}