package com.github.lucaengel.jass_entials.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.AuthUI.IdpConfig.GoogleBuilder
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import java.util.function.Consumer

/**
 * Class that handles the Google sign in process
 */
class GoogleAuthenticator {

    /**
     * Creates a sign in intent and launches it using the given launcher
     *
     * @param signInLauncher the launcher to use
     */
    private fun createSignInIntent(signInLauncher: ActivityResultLauncher<Intent>) {
        // Choose authentication providers
        val providers = listOf(
            GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    /**
     * Handles the result of the sign in process
     *
     * @param result the result of the sign in process
     * @param onSuccess the action to perform on success
     * @param onFailure the action to perform on failure
     */
    fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult?,
        onSuccess: Consumer<String?>?,
        onFailure: Consumer<String?>?
    ) {
        if (result == null) {
            onFailure!!.accept("login error")
        } else if (result.resultCode == Activity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
                ?: throw IllegalStateException("User is null")
            onSuccess!!.accept(user.email)
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // Sign in was cancelled by the user
            onFailure!!.accept("User cancelled sign in")
        } else {
            val error = result.idpResponse!!.error
            // Handle the error here
            // ...
            onFailure!!.accept("login error: $error")
        }
    }

    /**
     * Starts the sign in process
     *
     * @param signInLauncher the launcher to use
     */
    fun signIn(signInLauncher: ActivityResultLauncher<Intent>) {
        createSignInIntent(signInLauncher)
    }

    /**
     * Deletes the current user
     *
     * @param context the context to use
     * @param onComplete the action to perform on completion
     */
    fun delete(context: Context?, onComplete: Runnable?) {
        AuthUI.getInstance()
            .delete(context!!)
            .addOnCompleteListener { onComplete!!.run() }
    }

    /**
     * Signs out the current user
     *
     * @param context the context to use
     * @param onComplete the action to perform on completion
     */
    fun signOut(context: Context?, onComplete: Runnable?) {
        AuthUI.getInstance()
            .signOut(context!!)
            .addOnCompleteListener { onComplete!!.run() }
    }

    /**
     * Checks if the user is signed in
     *
     * @return true if the user is signed in, false otherwise
     */
    fun isSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }
}