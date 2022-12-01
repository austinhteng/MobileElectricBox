package com.example.eocproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.example.eocproject.databinding.ActivityMainBinding
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    companion object {
        private val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        fun localLevelFile(fileName : String): File {
            // Create the File where the photo should go
            val localPhotoFile = File(storageDir, "${fileName}")
            Log.d("MainActivity", "file path ${localPhotoFile.absolutePath}")
            return localPhotoFile
        }
    }
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