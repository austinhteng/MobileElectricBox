package com.example.eocproject.Catalog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.UUID

class CatalogViewModel : ViewModel() {
    private val storage = Storage()
    private val dbHelper = ViewModelDBHelper()
    private var levelsList: MutableLiveData<List<LevelMeta>> = MutableLiveData()


    fun uploadLevel(gridFile: File, wireFile: File, invFile: File, name: String) {
        val gridUUID = gridFile.name
        val wireUUID = wireFile.name
        val invUUID = invFile.name
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

    fun observeLevels() : LiveData<List<LevelMeta>> {
        return levelsList
    }

    fun getLevel(position: Int) : LevelMeta {
        return levelsList.value!![position]
    }

    fun initialFetch() {
        dbHelper.fetchLevelMeta(levelsList)
    }

    fun searchStorage(uuid: String, dest: File, flag: MutableLiveData<Boolean>) {
        val reference = storage.uuid2StorageReference(uuid)

        reference.getFile(dest).addOnSuccessListener {
            flag.postValue(true)
        }
    }

    val gridFileLoad = MutableLiveData<Boolean>(false)
    val wireFileLoad = MutableLiveData<Boolean>(false)
    val invFileLoad = MutableLiveData<Boolean>(false)
}