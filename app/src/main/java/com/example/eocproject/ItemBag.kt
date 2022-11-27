package com.example.eocproject

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.ImageView
import android.widget.LinearLayout

class ItemBag(var layout: LinearLayout, val context: Context) {
    private var itemsList: ArrayList<Item> = ArrayList()

    init {
    }

    fun initDragListener(view : ImageView) {
        //https://developer.android.com/develop/ui/views/touch-and-input/drag-drop
        view.tag = "Item"
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val item = ClipData.Item(v.tag as? CharSequence)
                val dragData = ClipData(
                    v.tag as? CharSequence,
                    arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                    item
                )
                val myShadow = DragShadowBuilder(v)

                v.startDragAndDrop(
                    dragData,  // The data to be dragged
                    myShadow,  // The drag shadow builder
                    v,
                    0          // Flags (not currently used, set to 0)
                )
            }
            true
        }

        view.setOnDragListener { v, event ->
            true
        }
    }

    fun initCreative() {
        Log.d("ItemBag", "InitCreative")

        //Add icons
        val size = (50 * context.getResources().getDisplayMetrics().density).toInt()
        val itemTypeList = arrayOf(Item.ItemType.SOURCE, Item.ItemType.DESTINATION, Item.ItemType.CABLE,
            Item.ItemType.LIGHT, Item.ItemType.PANEL)
        val sourceList = arrayOf(R.drawable.power_source, R.drawable.destination, R.drawable.cable,
            R.drawable.light_bulb, R.drawable.solar_panel)
        //TODO: Change x icon for clear button to an eraser

//        <a href="http://www.onlinewebfonts.com">oNline Web Fonts</a>
        for (i in 0 .. itemTypeList.size-1) {
            val item = Item(itemTypeList[i], Item.Direction.UP, context)
            itemsList.add(item)
            //make item images a seperate view class to implement numbers? Maybe just display duplicates.
            item.setImageResource(sourceList[i])
            item.layoutParams = LinearLayout.LayoutParams(size, size);
            initDragListener(item)
            layout.addView(item)
        }
    }
}