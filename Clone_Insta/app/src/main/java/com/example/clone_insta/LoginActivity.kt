package com.example.clone_insta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    var auth: FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        emailLoginBtn.setOnClickListener {
            signinAndSignup()
        }

        googleSigninBtn.setOnClickListener {
            googleLogin()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("973385658712-resbqjnntcgkvsb5cu6803c5953q5u7m.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    fun googleLogin() {
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess) {
                var account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                moveMainPage(task.result?.user)
            } else {
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signinAndSignup() {
        auth?.createUserWithEmailAndPassword(emailEditText.text.toString(), pwEditText.text.toString())?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                moveMainPage(task.result?.user)
            } else if(task.exception?.message.isNullOrEmpty()){
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            } else {
                signinEmail()
            }
        }
    }
    fun signinEmail() {
        auth?.signInWithEmailAndPassword(emailEditText.text.toString(), pwEditText.text.toString())?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                moveMainPage(task.result?.user)
            } else {
                Toast.makeText(this,task.exception?.message,Toast.LENGTH_LONG).show()
            }
        }
    }
    fun moveMainPage(user:FirebaseUser?) {
        if(user != null) {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}