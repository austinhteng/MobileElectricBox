package com.example.eocproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.example.eocproject.databinding.ActivityMainBinding

class DemoActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val viewModel : GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.observeDemoCleared().observeForever {
            if (it!!) {
                val returnIntent = Intent().apply {
                    putExtra(PlayGame.demoReturn, true)
                }
                setResult(AppCompatActivity.RESULT_OK, returnIntent)
                finish()
            }
        }

        supportFragmentManager.commit {
            viewModel.isCreative = false
            add(R.id.mainScreen, PlayGame.newInstance(), PlayGame.demoPlayTag)
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away

            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }
}