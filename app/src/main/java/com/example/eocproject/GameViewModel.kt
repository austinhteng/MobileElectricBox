package com.example.eocproject

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private var isRunning = MutableLiveData<Boolean>(false)
    private var creative = MutableLiveData<Boolean>(false)
    private var clearMode = MutableLiveData<Boolean>(false)

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
}