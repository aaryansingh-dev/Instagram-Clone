package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.databinding.ActivityLoginBinding
import com.example.instagramclone.databinding.ActivitySignupBinding
import com.example.instagramclone.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
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


    }

    private fun loginButtonInitialisation(){
        binding.loginLoginButton.setOnClickListener{
            if(binding.loginUsername.editText?.text.toString().equals("") or
                binding.loginPassword.editText?.text.toString().equals(""))
            {
                Toast.makeText(this@LoginActivity, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val email = binding.loginUsername.editText?.text.toString()
                val password = binding.loginPassword.editText?.text.toString()
                var user = User(email, password)

                Firebase.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful)
                    {
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(this@LoginActivity, it.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}