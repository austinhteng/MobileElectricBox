package com.example.eocproject

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.eocproject.databinding.PlayFragBinding

class PlayGame : Fragment() {
    companion object {
        fun newInstance() : PlayGame {
            return PlayGame()
        }

        val playFragTag = "playFragTag"
    }
    private lateinit var binding: PlayFragBinding
    private var rows = 10
    private var cols = 10
    private val viewModel: GameViewModel by activityViewModels()
    private lateinit var game : GameBoard

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = PlayFragBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        game = GameBoard(requireContext(), rows, cols, viewModel)
        binding.gameBoard.addView(game)
        val itemBag = ItemBag(binding.itemsHolder, requireContext())
        if (viewModel.getCreative()) {
            itemBag.initCreative()
        }

        viewModel.setIsRunning(false)
        Log.d("Play Game Fragment", "view Created")

        viewModel.setClearMode(false)
        binding.trashButton.setOnClickListener {
            viewModel.setClearMode(!viewModel.getClearMode())
        }
        viewModel.observeClearMode().observe(viewLifecycleOwner) {
            if (!it) {
                binding.trashButton.setBackgroundColor(Color.LTGRAY)
            } else {
                binding.trashButton.setBackgroundColor(Color.RED)
            }
            Log.d("ClearMode", viewModel.getClearMode().toString())
        }

        binding.startBut.setBackgroundColor(Color.GREEN)
        binding.startBut.text = "Power On"
        binding.startBut.setOnClickListener {
            if (binding.startBut.text == "Power On") {
                game.startGame()
                binding.startBut.text = "Power Off"
                binding.startBut.setBackgroundColor(Color.RED)
            } else {
                game.clearPower()
                binding.startBut.text = "Power On"
                binding.startBut.setBackgroundColor(Color.GREEN)
            }
        }
        Log.d("ClearMode", viewModel.getClearMode().toString())
    }
}