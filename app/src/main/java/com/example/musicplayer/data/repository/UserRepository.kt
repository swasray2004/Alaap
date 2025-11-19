package com.example.musicplayer.data.repository

import com.example.musicplayer.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, displayName: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            result.user?.updateProfile(profileUpdates)?.await()

            // Save user to database
            val user = User(
                uid = result.user?.uid ?: "",
                email = email,
                displayName = displayName
            )
            database.reference.child("users").child(user.uid).setValue(user).await()

            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> {
        return try {
            val profileUpdates = UserProfileChangeRequest.Builder().apply {
                displayName?.let { setDisplayName(it) }
                photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
            }.build()

            currentUser?.updateProfile(profileUpdates)?.await()

            // Update in database
            val updates = mutableMapOf<String, Any>()
            displayName?.let { updates["displayName"] = it }
            photoUrl?.let { updates["photoUrl"] = it }

            currentUser?.uid?.let {
                database.reference.child("users").child(it).updateChildren(updates).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
