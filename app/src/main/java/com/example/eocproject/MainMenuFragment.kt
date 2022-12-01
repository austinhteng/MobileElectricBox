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
import androidx.fragment.app.viewModels
import com.example.eocproject.databinding.ContentHomeBinding

class MainMenuFragment: Fragment() {
    companion object {
        fun newInstance(): MainMenuFragment {
            return MainMenuFragment()
        }

        val mainMenuFragTag = "mainMenuFragTag"
    }
    private lateinit var binding : ContentHomeBinding
    private val gameViewModel : GameViewModel by activityViewModels()


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
            parentFragmentManager.commit {
                val frag = PlayGame.newInstance(true)
                add(R.id.mainScreen, frag, PlayGame.playFragTag)
                addToBackStack(PlayGame.playFragTag)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                show(frag)
                hide(parentFragmentManager.findFragmentByTag(mainMenuFragTag)!!)
            }
        }
        Log.d("Main Menu", "view Created")
    }
}