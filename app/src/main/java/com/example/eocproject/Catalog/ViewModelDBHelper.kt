package com.example.eocproject.Catalog

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val rootCollection = "allLevels"

    fun fetchLevelMeta(levelsList: MutableLiveData<List<LevelMeta>>) {
        dbFetchLevelMeta(levelsList)
    }
    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    // But be careful about how listener updates live data
    // and noteListener?.remove() in onCleared
    private fun limitAndGet(query: Query,
                            levelsList: MutableLiveData<List<LevelMeta>>
    ) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "allNotes fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                levelsList.postValue(result.documents.mapNotNull {
                    it.toObject(LevelMeta::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "allNotes fetch FAILED ", it)
            }
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/order-limit-data
    private fun dbFetchLevelMeta(levelsList: MutableLiveData<List<LevelMeta>>) {
        var query = db.collection(rootCollection)
        limitAndGet(query, levelsList)
    }

    // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
    fun createLevelMeta(
        levelMeta: LevelMeta,
        levelsList: MutableLiveData<List<LevelMeta>>
    ) {
        // You can get a document id if you need it.
        //levelMeta.firestoreID = db.collection(rootCollection).document().id
        // XXX Write me: add levelMeta
        db.collection(rootCollection).add(levelMeta)
        levelMeta.firestoreID = db.collection(rootCollection).document().id
        fetchLevelMeta(levelsList)
    }
}