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
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.eocproject.databinding.PlayFragBinding
import org.w3c.dom.Text

class PlayGame(val isCreative: Boolean) : Fragment() {
    companion object {
        fun newInstance(isCreative: Boolean) : PlayGame {
            return PlayGame(isCreative)
        }

        val playFragTag = "playFragTag"
        val demoPlayTag = "demoPlayTag"
        val gridTag = "gridTag"
        val wireTag = "wireTag"
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments

        game = GameBoard(requireContext(), rows, cols, viewModel)
        if (bundle != null) {
            game.unpack(bundle)
        }

        binding.gameBoard.addView(game)

        binding.inventory.setBackgroundColor(Color.LTGRAY)
        val itemBag = ItemBag(binding.inventory, requireContext(), viewModel)
        itemBag.initInventory(viewLifecycleOwner)

        if (isCreative) {
            binding.creativeLabel.text = "Creative Inventory:"
            binding.creativeItemBag.setBackgroundColor(Color.LTGRAY)
            val creativeBag = ItemBag(binding.creativeItemBag, requireContext(), viewModel)
            creativeBag.initCreative()

            binding.demoBut.visibility = View.VISIBLE
            binding.demoBut.setOnClickListener {  }

            binding.uploadButton.visibility = View.VISIBLE
            binding.uploadButton.setOnClickListener {  }

            viewModel.observeRunning().observe(viewLifecycleOwner) {
                if (it!!) {
                    binding.demoBut.isClickable = false
                    binding.uploadButton.isClickable = false
                } else {
                    binding.demoBut.isClickable = true
                    binding.uploadButton.isClickable = true
                }
            }
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
                game.endGame()
                binding.startBut.text = "Power On"
                binding.startBut.setBackgroundColor(Color.GREEN)
            }
        }

        //Start new fragment of simulating user playing.
        binding.demoBut.setOnClickListener {
            Log.d("demo", "start demo")
            game.returnInv()
            val grid = game.getGrid()
            val wire = game.getWireGrid()
            val inv = viewModel.getInventory()
            var bundle = Bundle().apply {
                putParcelable(gridTag, grid)
                putParcelable(wireTag, wire)
            }

//            Log.d("test", grid.readBytes().size.toString())
//            game.loadGrid(grid, wire)
//            viewModel.loadInventory(inv)
            parentFragmentManager.commit {  //Start playfrag in user mode.
                val frag = newInstance(false)
                frag.arguments = bundle
                viewModel.resetState()
                viewModel.setInventory(inv)
                add(R.id.mainScreen, frag, demoPlayTag)
                addToBackStack(demoPlayTag)
                setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                show(frag)
                hide(parentFragmentManager.findFragmentByTag(playFragTag)!!)
            }
        }
    }


}