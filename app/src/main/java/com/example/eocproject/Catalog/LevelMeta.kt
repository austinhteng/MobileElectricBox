package com.example.eocproject.Catalog

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class LevelMeta(
    // Auth information
    var levelName: String = "",
    var gridUUID: String = "",
    var wireUUID : String = "",
    var invUUID : String = "",
    // Written on the server
    @ServerTimestamp val timeStamp: Timestamp? = null,
    // firestoreID is generated by firestore, used as primary key
    @DocumentId var firestoreID: String = ""
)