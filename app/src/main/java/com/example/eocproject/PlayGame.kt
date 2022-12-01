package com.example.eocproject

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.eocproject.Catalog.CatalogViewModel
import com.example.eocproject.Catalog.ViewModelDBHelper
import com.example.eocproject.databinding.PlayFragBinding


class PlayGame() : Fragment() {
    companion object {
        fun newInstance() : PlayGame {
            return PlayGame()
        }

        val playFragTag = "playFragTag"
        val demoPlayTag = "demoPlayTag"
        val gridTag = "gridTag"
        val wireTag = "wireTag"
        val invTag =  "invTag"
        val demoReturn = "demoReturn"
        val nameTag = "nameTag"
    }
    private val viewModel: GameViewModel by activityViewModels()
    private val catalogViewModel : CatalogViewModel by activityViewModels()
    private var levelName = ""
    private lateinit var binding: PlayFragBinding
    private var rows = 10
    private var cols = 10

    private lateinit var game : GameBoard
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Get the data that was returned, and display it
            //SSS
            val data: Intent? = result.data
            data?.extras?.apply {
                viewModel.setDemoCleared(data.getBooleanExtra(demoReturn, false))
            }
            //EEE // XXX Write me
        } else {
            Log.w(javaClass.simpleName, "Bad activity return code ${result.resultCode}")
        }
    }

    private var nameLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Get the data that was returned, and display it
            //SSS
            val data: Intent? = result.data
            data?.extras?.apply {
                levelName = data.getStringExtra(nameTag)!!
            }
            //EEE // XXX Write me
        } else {
            Log.w(javaClass.simpleName, "Bad activity return code ${result.resultCode}")
        }
    }


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

        val bundle = requireActivity().intent

        game = GameBoard(requireContext(), rows, cols, viewModel)
        if (bundle.hasExtra(gridTag) && bundle.hasExtra(wireTag) && bundle.hasExtra(invTag)) {
            game.unpack(bundle.extras!!)
            viewModel.setInventory(bundle.getIntArrayExtra(invTag)!!)
        }

        binding.gameBoard.addView(game)

        binding.inventory.setBackgroundColor(Color.LTGRAY)
        val itemBag = ItemBag(binding.inventory, requireContext(), viewModel)
        itemBag.initInventory(viewLifecycleOwner)

        if (viewModel.isCreative) {
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

            viewModel.observeDemoCleared().observe(viewLifecycleOwner) {
                if (it!!) {
                    binding.uploadButton.setOnClickListener {
                        val name = promptName()

                        uploadLevel(name)
                    }
                } else {
                    binding.uploadButton.setOnClickListener {
                        val toast = Toast(context)
                        toast.setText("Please complete the level in demo to upload.")
                        toast.show()
                    }
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

            val demo = Intent(context, DemoActivity::class.java)

            demo.putExtra(gridTag, grid)
            demo.putExtra(wireTag, wire)
            demo.putExtra(invTag, inv)

            resultLauncher.launch(demo)
        }
    }

    fun promptName() : String {
        val name = Intent(context, NamePromptActivity::class.java)

        nameLauncher.launch(name)
        return levelName
    }
    fun uploadLevel(name: String) {
        game.returnInv()
        val gridFile = game.exportGrid()
        val wireFile = game.exportWireGrid()
        val invFile = viewModel.exportInventory()

        catalogViewModel.uploadLevel(gridFile, wireFile, invFile, name)
    }
}