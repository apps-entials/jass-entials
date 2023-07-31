package com.github.lucaengel.jass_entials

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.github.lucaengel.jass_entials.auth.GoogleAuthenticator
import com.github.lucaengel.jass_entials.game.SelectGameActivity
import com.github.lucaengel.jass_entials.ui.theme.JassentialsTheme

class SignInActivity : ComponentActivity() {

    private var currentUserEmail = ""

    private lateinit var authenticator: GoogleAuthenticator

    // Register the launcher to handle the result of Google Authentication
    private val signInLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        authenticator.onSignInResult(
            res,
            onSuccess = { email ->
                // Should not be null on success
                currentUserEmail = email!!
                val intent = Intent(this, SelectGameActivity::class.java)
                    .putExtra("email", currentUserEmail)
                startActivity(intent)
            },
            onFailure = {

            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authenticator = GoogleAuthenticator()

        setContent {
            JassentialsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SignInForm()
                }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun SignInForm() {
        LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Button(
                onClick = {
                    authenticator.signIn(signInLauncher)
                },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Sign in",
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    currentUserEmail = "GUEST"
                    val intent = Intent(this@SignInActivity, SelectGameActivity::class.java)
                        .putExtra("email", currentUserEmail)
                    startActivity(intent)
                },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = "Continue as guest")
            }
        }
    }
}