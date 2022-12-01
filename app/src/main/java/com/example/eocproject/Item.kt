package com.example.eocproject

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.example.eocproject.databinding.ItemIconBinding


enum class Direction(val value: Int) {
    LEFT(0), UP(1), RIGHT(2), DOWN(3);
    companion object {
        fun fromInt(value: Int) = Direction.values().first { it.value == value }
    }
}
enum class ItemType(val value: Int) {
    EMPTY(0), LIGHT(1), SOURCE(2), DESTINATION(3), CABLE(4), PANEL(5);
    companion object {
        fun fromInt(value: Int) = ItemType.values().first { it.value == value }
    }
}

enum class Origin(val value: Int) {
    USER(0), GAMEBOARD(1);
    companion object {
        fun fromInt(value: Int) = Origin.values().first { it.value == value }
    }
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