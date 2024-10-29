package com.example.instagramclone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode.Callback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.databinding.ActivitySignupBinding
import com.example.instagramclone.model.User
import com.example.instagramclone.utils.USER_NODE
import com.example.instagramclone.utils.USER_PROFILE_FOLDER
import com.example.instagramclone.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class SignupActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignupBinding.inflate(layoutInflater)
    }

    private lateinit var user: User
    private var profileImageUri: Uri? = null

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        uri?.let {
            profileImageUri = uri
            Log.d(
                "SignUpActivity",
                "URI added: $uri"
            )
            binding.signUpProfileImageView.setImageURI(profileImageUri)
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

        user = User()

        checkIntent(intent)
        addImageButtonInitialisation()
        signupButtonInitialisation()
        loginButtonInitialisation()
        changeLoginColor()
    }

    private fun loginButtonInitialisation(){
        binding.signUpPreLogInTextView.setOnClickListener{
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
    }
    private fun addImageButtonInitialisation()
    {
        binding.signUpAddImage.setOnClickListener{
            launcher.launch("image/*")
        }
    }
    private fun signupButtonInitialisation() {
        binding.signUpSignUpButton.setOnClickListener {

            // if we have some intent for Edit Profile
            if (intent.hasExtra("MODE")) {
                if (intent.getIntExtra("MODE", -1) == 1) {
                    if (profileImageUri != null) {
                        binding.loadingPanel.visibility = View.VISIBLE;
                        uploadImage(profileImageUri!!, USER_PROFILE_FOLDER) {
                            if (it != null) {
                                user.image = it.toString()
                                updateFirestoreEntry {
                                    finish()
                                }
                            }
                        }
                    }
                } else {
                    if (binding.signUpUsername.editText?.text.toString().equals("") or
                        binding.signUpEmail.editText?.text.toString().equals("") or
                        binding.signUpPassword.editText?.text.toString().equals("")
                    ) {
                        Toast.makeText(
                            this@SignupActivity,
                            "Please fill all the fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // upload the image from the profileImageUri if not null
                        if (profileImageUri != null) {
                            binding.loadingPanel.visibility = View.VISIBLE;
                            uploadImage(profileImageUri!!, USER_PROFILE_FOLDER) { it ->
                                if (it != null) {
                                    user.image = it.toString()
                                    createFirestoreEntry {
                                        startActivity(
                                            Intent(
                                                this@SignupActivity,
                                                HomeActivity::class.java
                                            )
                                        )
                                    }
                                }
                            }
                        } else {
                            createFirestoreEntry {
                                startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
                            }
                        }

                    }
                }


            }
        }
    }

        private fun createFirestoreEntry(callback: () -> Unit) {
            // create the firestore entry and store authentication data in auth folder
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.signUpEmail.editText?.text.toString(),
                binding.signUpPassword.editText?.text.toString()
            ).addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    updateFirestoreEntry {
                        callback()
                    }

                } else {
                    Toast.makeText(
                        this@SignupActivity,
                        result.exception?.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        private fun updateFirestoreEntry(callback: () -> Unit){
            user.name = binding.signUpUsername.editText?.text.toString()
            user.email = binding.signUpEmail.editText?.text.toString()
            user.password = binding.signUpPassword.editText?.text.toString()

            Firebase.firestore.collection(USER_NODE)
                .document(Firebase.auth.currentUser!!.uid).set(user)
                .addOnSuccessListener {
                    binding.loadingPanel.visibility = View.GONE
                    Toast.makeText(
                        this@SignupActivity,
                        "Signup Successful",
                        Toast.LENGTH_SHORT
                    ).show()
                    callback()
                }
        }

        private fun changeLoginColor() {
            val text =
                "<font color=#ff000000>Already have an account?<font> <font color=#1E88E5>Login</font>"
            binding.signUpPreLogInTextView.text = Html.fromHtml(text)
        }

        private fun checkIntent(intent: Intent) {
            if (intent.hasExtra("MODE")) {
                if (intent.getIntExtra("MODE", -1) == 1) {
                    binding.signUpSignUpButton.setText(R.string.edit_profile)
                    binding.signUpEmail.editText?.setText(intent.getStringExtra("EMAIL"))
                    binding.signUpUsername.editText?.setText(intent.getStringExtra("USERNAME"))
                    binding.signUpPassword.editText?.setText(intent.getStringExtra("PASSWORD"))

                    if (intent.getStringExtra("PROFILE_URL") != null) {
                        Picasso.get().load(intent.getStringExtra("PROFILE_URL"))
                            .into(binding.signUpProfileImageView)
                    }

                    binding.signUpPreLogInTextView.visibility = View.INVISIBLE
                }
            }
        }

}


