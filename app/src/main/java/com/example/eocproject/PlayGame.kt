package com.example.eocproject

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.eocproject.databinding.PlayFragBinding
import org.w3c.dom.Text

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        game = GameBoard(requireContext(), rows, cols, viewModel)
        binding.gameBoard.addView(game)

        binding.inventory.setBackgroundColor(Color.LTGRAY)
        val itemBag = ItemBag(binding.inventory, requireContext(), viewModel)
        itemBag.initInventory(viewLifecycleOwner)

        if (viewModel.getCreative()) {
            binding.creativeLabel.text = "Creative Inventory:"
            binding.creativeItemBag.setBackgroundColor(Color.LTGRAY)
            val creativeBag = ItemBag(binding.creativeItemBag, requireContext(), viewModel)
            creativeBag.initCreative()
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
        binding.startBut.text = "On"
        binding.startBut.setOnClickListener {
            if (binding.startBut.text == "On") {
                game.startGame()
                binding.startBut.text = "Off"
                binding.startBut.setBackgroundColor(Color.RED)
            } else {
                game.clearPower()
                binding.startBut.text = "On"
                binding.startBut.setBackgroundColor(Color.GREEN)
            }
        }
        Log.d("ClearMode", viewModel.getClearMode().toString())
    }
}