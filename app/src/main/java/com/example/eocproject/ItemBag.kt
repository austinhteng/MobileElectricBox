package com.example.eocproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

class ItemBag(val layout: LinearLayout, context: Context) {
    private var itemsList: ArrayList<Item> = ArrayList()

    init {
        itemsList.add(Item(Item.ItemType.LIGHT, Item.Direction.UP))

        val itemImage = ImageView(context)
        itemImage.setImageResource(R.drawable.ic_clear_black_24dp)

        layout.addView(itemImage)
        update()
    }

    fun update() {

    }
}