package com.example.eocproject.Game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eocproject.MainActivity
import java.io.File

class GameViewModel : ViewModel() {
    private var isRunning = MutableLiveData<Boolean>(false)
    private var clearMode = MutableLiveData<Boolean>(false)
    private var inventory = MutableLiveData<IntArray>().apply {
        value = IntArray(ItemBag.itemTypeList.size)
    }
    private var demoCleared = MutableLiveData<Boolean>(false)
    var isCreative = false

    fun resetState() {
        isRunning.value = false
        clearMode.value = false
        inventory.value = IntArray(ItemBag.itemTypeList.size)
        demoCleared.value = false
        isCreative = false
    }

    fun getIsRunning() : Boolean {
        return isRunning.value!!
    }

    fun setIsRunning(newVal : Boolean) {
        isRunning.value = newVal
    }

    fun observeRunning() : LiveData<Boolean> {
        return isRunning
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

    fun getDemoCleared() : Boolean {
        return demoCleared.value!!
    }

    fun setDemoCleared(newVal : Boolean) {
        demoCleared.value = newVal
    }

    fun observeDemoCleared() : LiveData<Boolean> {
        return demoCleared
    }

    fun resetInventory() {
        inventory.value = IntArray(ItemBag.itemTypeList.size)
    }

    fun getInventory() : IntArray {
        return inventory.value!!
    }

    fun setInventory(inv : IntArray) {
        inventory.value = inv
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

    private fun write4BytesToBuffer(data: Int) : ByteArray {
        val buffer = ByteArray(4)
        buffer[0] = (data shr 0).toByte()
        buffer[1] = (data shr 8).toByte()
        buffer[2] = (data shr 16).toByte()
        buffer[3] = (data shr 24).toByte()
        return buffer
    }

    private fun read4BytesFromBuffer(buffer: ByteArray, offset: Int): Int {
        return (buffer[offset + 3].toInt() shl 24) or
                (buffer[offset + 2].toInt() and 0xff shl 16) or
                (buffer[offset + 1].toInt() and 0xff shl 8) or
                (buffer[offset + 0].toInt() and 0xff)
    }

    //Must be called after Gameboard export wireGrid && export grid is called
    fun exportInventory() : File {
        val fileInv = MainActivity.localLevelFile("tempInv")
        val inv = inventory.value!!
        for (element in inv) {
            fileInv.writeBytes(write4BytesToBuffer(element))
        }
        return fileInv
    }

    fun loadInventory(invFile : File) {
        resetInventory()
        var newInv = IntArray(inventory.value!!.size)
        val buffer = invFile.readBytes()

        for (i in 0 until newInv.size / 4) {
            newInv[i] = read4BytesFromBuffer(buffer, i*4)
        }
    }
}