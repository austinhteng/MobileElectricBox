package com.example.eocproject.Game

import android.content.Context
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.DragShadowBuilder
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import com.example.eocproject.R
import com.google.android.flexbox.FlexboxLayout

class ItemBag(var layout: FlexboxLayout, val context: Context, val viewModel: GameViewModel) {
    private var isCreative = false
    val size = (50 * context.getResources().getDisplayMetrics().density).toInt()

    companion object {
        val itemTypeList = arrayOf(
            ItemType.SOURCE, ItemType.DESTINATION, ItemType.CABLE,
            ItemType.LIGHT, ItemType.PANEL
        )
        val sourceList = arrayOf(
            R.drawable.power_source, R.drawable.destination, R.drawable.cable,
            R.drawable.light_bulb, R.drawable.solar_panel
        )
    }

    init {
    }

    private fun initDragListener(view : Item) {
        //https://developer.android.com/develop/ui/views/touch-and-input/drag-drop
        view.tag = "Item"
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (viewModel.getIsRunning()) { //Can't modify state while game runs.
                    return@setOnTouchListener true
                }

                if (!isCreative && viewModel.getClearMode() && viewModel.isCreative) {
                    viewModel.removeItem(view.type)
                    return@setOnTouchListener true
                }

                val myShadow = DragShadowBuilder(v)
                val itemInfo = if (isCreative) ItemData(view.type, view.direction, Origin.GAMEBOARD)
                    else ItemData(view.type, view.direction, Origin.USER)
                v.startDragAndDrop(
                    null,  // The data to be dragged
                    myShadow,  // The drag shadow builder
                    itemInfo,
                    0          // Flags (not currently used, set to 0)
                )

                if (!isCreative) {  //Sourced from inventory
                    viewModel.removeItem(view.type)
                }
            }
            true
        }
    }

    fun initCreative() {
        isCreative = true

        //Add icons to layout
        for (i in 0 .. itemTypeList.size-1) {
            val item = Item(itemTypeList[i], Direction.UP, context)
            //make item images a seperate view class to implement numbers? Maybe just display duplicates.
            item.setSrc(sourceList[i])
            item.layoutParams = LinearLayout.LayoutParams(size, size);
            initDragListener(item)
            layout.addView(item)
        }

        layout.setOnDragListener { v, event ->
            //Prevents shadow from jumping back
            true
        }
    }

    fun initInventory(owner: LifecycleOwner) {
        isCreative = false

        viewModel.observeInventory().observe(owner) {
            layout.removeAllViews()
            drawInventory(it)
            drawMinimumLayoutSize()
        }

        layout.setOnDragListener { v, event ->
            val data = event.localState as ItemData
            if (event.action == DragEvent.ACTION_DROP) {    //Items dropped into into inventory
                viewModel.addItem(data.type)
            }
            true
        }


    }
    // Draws items into the ItemBag layout based on itemCounts
    private fun drawInventory(itemCounts: IntArray) {
        for (i in 0 until itemCounts.size) {
            val count = itemCounts[i]
            if (count != 0) {
                val item = Item(itemTypeList[i], Direction.UP, context)
                item.setSrc(sourceList[i])
                item.layoutParams = LinearLayout.LayoutParams(size, size)
                initDragListener(item)
                initTouchToClear(item)
                item.setText(count.toString())
                item.invalidate()
                layout.addView(item)
            }
        }
    }

    private fun drawMinimumLayoutSize() {
        if (layout.childCount == 0) {   //Ensure a child exists so the layout takes space.
            val empty = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(size, size)
                visibility = View.INVISIBLE
            }
            layout.addView(empty)
        }
    }

    private fun initTouchToClear(item: Item) {
        item.setOnClickListener {
            if (viewModel.getClearMode() && !viewModel.getIsRunning() && viewModel.isCreative) {
                viewModel.removeItem(item.type)
            }
        }
    }
}