package com.example.eocproject

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eocproject.databinding.NamePromptBinding

class NamePromptActivity : AppCompatActivity() {
    companion object {
        val nameReturn = "nameReturn"
    }

    lateinit var binding: NamePromptBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        binding = NamePromptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enter.setOnClickListener {
            if (!binding.name.text.isNullOrEmpty()) {
                val returnIntent = Intent().apply {
                    putExtra(nameReturn, true)
                }
                setResult(AppCompatActivity.RESULT_OK, returnIntent)
                finish()
            }
        }
    }
}