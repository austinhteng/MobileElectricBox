package com.example.eocproject.Catalog

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class Storage {
    // Create a storage reference from our app
    private val levelStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

    // https://firebase.google.com/docs/storage/android/upload-files#upload_from_a_local_file
    fun uploadFile(localFile: File, uuid: String, uploadSuccess:()->Unit) {
        // XXX Write me
        var file = Uri.fromFile(localFile)
        val riversRef = levelStorage.child("${file.lastPathSegment}")
        var uploadTask = riversRef.putFile(file)

        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnFailureListener {
                // Handle unsuccessful uploads
                if(localFile.delete()) {
                    Log.d(javaClass.simpleName, "Upload FAILED $uuid, file deleted")
                } else {
                    Log.d(javaClass.simpleName, "Upload FAILED $uuid, file delete FAILED")
                }
            }
            .addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                uploadSuccess()
                if(localFile.delete()) {
                    Log.d(javaClass.simpleName, "Upload succeeded $uuid, file deleted")
                } else {
                    Log.d(javaClass.simpleName, "Upload succeeded $uuid, file delete FAILED")
                }
            }
    }
    // https://firebase.google.com/docs/storage/android/delete-files#delete_a_file
    fun deleteImage(pictureUUID: String) {
        // Delete the file
        // XXX Write me
        val desertRef = levelStorage.child(pictureUUID)
        desertRef.delete()
    }

    fun uuid2StorageReference(uuid: String): StorageReference {
        return levelStorage.child(uuid)
    }
}