package com.example.eocproject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.TreeMap

class GameViewModel : ViewModel() {
    private var isRunning = MutableLiveData<Boolean>(false)
    private var creative = MutableLiveData<Boolean>(false)
    private var clearMode = MutableLiveData<Boolean>(false)
    private var inventory = MutableLiveData<IntArray>().apply {
        value = IntArray(ItemBag.itemTypeList.size)
    }
    private var demoCleared = MutableLiveData<Boolean>(false)

    fun resetState() {
        isRunning.value = false
        creative.value = false
        clearMode.value = false
        inventory.value = IntArray(ItemBag.itemTypeList.size)
        demoCleared.value = false
    }

    fun getIsRunning() : Boolean {
        return isRunning.value!!
    }

    fun setIsRunning(newVal : Boolean) {
        isRunning.value = newVal
    }

    fun getCreative() : Boolean {
        return creative.value!!
    }

    fun setCreative(newVal : Boolean) {
        creative.value = newVal
    }

    fun getClearMode() : Boolean {
        return clearMode.value!!
    }

    fun setClearMode(newVal : Boolean) {
        clearMode.value = newVal
    }

    fun observeClearMode() : LiveData<Boolean> {
        return clearMode
    }

    fun resetInventory() {
        inventory.value = IntArray(ItemBag.itemTypeList.size)
    }

    fun addItem(item: ItemType) {
        inventory.value!![ItemBag.itemTypeList.indexOf(item)]++
        inventory.value = inventory.value //notify change
    }

    fun removeItem(item: ItemType) {
        val index = ItemBag.itemTypeList.indexOf(item)
        if (inventory.value!![index] == 0) {
            Log.e("Inventory", "Trying to remove an item that doesn't exist in inventory.")
        } else {
            inventory.value!![index]--
        }
        inventory.value = inventory.value //notify change
    }

    fun observeInventory() : LiveData<IntArray> {
        return inventory
    }
}