package com.example.eocproject

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.eocproject.databinding.ItemIconBinding


enum class Direction {
    LEFT, UP, RIGHT, DOWN
}
enum class ItemType {
    EMPTY, LIGHT, SOURCE, DESTINATION, CABLE, PANEL
}

enum class Origin {
    USER, GAMEBOARD
}

class Item (var type : ItemType, var direction : Direction, context: Context) : RelativeLayout(context) {
    private val binding : ItemIconBinding
    init {
        val inflater: LayoutInflater
        inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemIconBinding.inflate(inflater) //TODO: There's a difference between the ItemIcon binding and the binding of the actual extended layout
        this.addView(binding.itemIcon)
    }

    fun setSrc(src: Int) {
        binding.icon.setImageResource(src)
    }

    fun setText(text: String) {
        binding.text.setTextColor(Color.BLUE)
        binding.text.text = text
    }
}

class ItemData(var type: ItemType, var direction: Direction, var origin: Origin)