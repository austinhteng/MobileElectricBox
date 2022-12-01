package com.example.eocproject.Catalog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.UUID

class CatalogViewModel : ViewModel() {
    private val storage = Storage()
    private val dbHelper = ViewModelDBHelper()
    private var levelsList: MutableLiveData<List<LevelMeta>> = MutableLiveData()


    fun uploadLevel(gridFile: File, wireFile: File, invFile: File, name: String) {
        val gridUUID = UUID.randomUUID().toString()
        val wireUUID = UUID.randomUUID().toString()
        val invUUID = UUID.randomUUID().toString()
        storage.uploadFile(gridFile, gridUUID) {}
        storage.uploadFile(wireFile, wireUUID) {}
        storage.uploadFile(invFile, invUUID) {}
        createLevelMeta(name, gridUUID, wireUUID, invUUID)
    }

    fun createLevelMeta(title: String, gUUID : String, wUUID : String, iUUID : String) {
        val levelMeta = LevelMeta(
            levelName=title,
            gridUUID = gUUID,
            wireUUID = wUUID,
            invUUID = iUUID
        )
        dbHelper.createLevelMeta(levelMeta, levelsList)
    }
}