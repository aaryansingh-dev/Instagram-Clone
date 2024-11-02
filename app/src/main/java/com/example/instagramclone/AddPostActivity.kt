package com.example.instagramclone

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.databinding.ActivityAddPostBinding
import com.example.instagramclone.databinding.ActivitySignupBinding
import com.example.instagramclone.model.Post
import com.example.instagramclone.utils.POST_FOLDER
import com.example.instagramclone.utils.USER_POST_FOLDER
import com.example.instagramclone.utils.uploadImage
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddPostActivity : AppCompatActivity() {

    companion object{
        private const val ACTIVITY_NAME = "AddPostActivity"
    }
    private val binding by lazy {
        ActivityAddPostBinding.inflate(layoutInflater)
    }

    private var postImageUri: Uri? = null
    private var post: Post? = null
    private var userPost:String? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){uri->
        uri?.let {
            postImageUri = uri
            Log.d(
                "AddPostActivity",
                "URI added: $uri"
            )
            binding.addPostImage.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.addPostMaterialToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.addPostMaterialToolbar.setNavigationOnClickListener{
            finish()
        }


        onClickImageView()
        onUploadButtonClick()


    }

    private fun onClickImageView(){
        binding.addPostImage.setOnClickListener{
            launcher.launch("image/*")
        }
    }

    private fun onUploadButtonClick()
    {
        binding.addPostUploadButton.setOnClickListener{
            if(postImageUri == null){
                Toast.makeText(baseContext, "Please add an image.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.loadingPanel.visibility = View.VISIBLE
            uploadPost(){
                Log.d("AddPostActivity", "Post Added to the folders")
                Toast.makeText(baseContext, "Post added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun uploadPost(callback:() -> Unit){
        uploadImage(postImageUri!!, POST_FOLDER){url ->
            Log.d("AddPostActivity", "Post added to folder: $POST_FOLDER")
            if(url != null){
                val doc = Firebase.firestore.collection(POST_FOLDER).document()
                val authorId = Firebase.auth.currentUser!!.uid
                post = Post(authorId, url, binding.addPostCaption.editText?.text.toString())
                doc.set(post!!).addOnSuccessListener {
                    Log.d("AddPostActivity", "Post Entry added to firestore with id: ${doc.id}")
                    updateUserPost(doc.id.toString(), authorId){
                        callback()
                    }
                }


            }
        }

    }

    private fun updateUserPost(postId: String, userId: String, callback:()->Unit){
        val doc = Firebase.firestore.collection(USER_POST_FOLDER).document(userId)
        doc.get().addOnSuccessListener { d->
            if(d.exists()){
                doc.update("postList", FieldValue.arrayUnion(postId))
                    .addOnSuccessListener {
                        Log.d("AddPostActivity", "List updated.")
                        callback()}
                    .addOnFailureListener{
                        Log.d(ACTIVITY_NAME, "Failed to update the list")
                        callback()}
            }
            else{
                Log.i(ACTIVITY_NAME, "Making document with id ${doc.id}")

                val data = mapOf("postList" to listOf(postId))
                doc.set(data)
                    .addOnSuccessListener {
                        Log.d(ACTIVITY_NAME, "New document created")
                        callback()}
                    .addOnFailureListener{
                        Log.d(ACTIVITY_NAME, "Failed to create a new document")
                        callback()}
            }
        }
    }
}