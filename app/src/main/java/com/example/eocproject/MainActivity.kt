package com.example.eocproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eocproject.databinding.ActivityMainBinding
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.commit {
            add(R.id.mainScreen, MainMenuFragment.newInstance(), MainMenuFragment.mainMenuFragTag)
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away

            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        }
    }
}