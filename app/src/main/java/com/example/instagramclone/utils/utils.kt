package com.example.instagramclone.utils

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImage(uri: Uri, folderName:String, callback:(String?) -> Unit){
    var imageUrl:String? = null

    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString()).putFile(uri)
        .addOnSuccessListener { it ->
            it.storage.downloadUrl.addOnSuccessListener { it ->
                imageUrl = it.toString()
                callback(imageUrl)
            }
        }
}



