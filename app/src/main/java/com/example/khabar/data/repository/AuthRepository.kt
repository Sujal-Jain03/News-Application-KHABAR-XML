package com.example.khabar.data.repository


import android.util.Log
import com.example.khabar.data.model.User

import com.example.khabar.ui.auth.Result
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
@Suppress("DEPRECATION")
class AuthRepository
@Inject
constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        profilePictureUrl: String?
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Create user in Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return@withContext Result.Error("Failed to create user account")

            // Create user object
            val user = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                fullName = fullName,
                phoneNumber = phoneNumber,
                profilePictureUrl = profilePictureUrl,
                createdAt = System.currentTimeMillis()
            )

            // Save to Firestore with error handling
            try {
                firestore.collection("USERS")
                    .document(firebaseUser.uid)
                    .set(user)
                    .await()

                return@withContext Result.Success(user)

            } catch (firestoreException: Exception) {
                // If Firestore save fails, delete the auth user to maintain consistency
                firebaseUser.delete().await()
                return@withContext Result.Error("Failed to save user data: ${firestoreException.message}")
            }

        } catch (e: Exception) {
            return@withContext Result.Error(e.message ?: "Unknown error occurred")
        }
    }


    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val result: AuthResult =
                    auth
                        .signInWithEmailAndPassword(
                            email,
                            password,
                        ).await()
                result.user?.let { firebaseUser ->
                    val userDoc: DocumentSnapshot =
                        firestore
                            .collection("USERS")
                            .document(firebaseUser.uid)
                            .get()
                            .await()
                    if (userDoc.exists()) {
                        Result.Success(userDoc.toObject(User::class.java)!!)
                    } else {
                        Result.Error("User not found")
                    }
                } ?: Result.Error("Authentication failed")
            } catch (e: Exception) {
                Result.Error(e.message ?: "Unknown error")
            }
        }

    fun signOut() {
        auth.signOut()
    }

    suspend fun getCurrentUser(): User? = auth.currentUser?.let { firebaseUser ->
        try {
            val userDoc: DocumentSnapshot =
                firestore
                    .collection("USERS")
                    .document(firebaseUser.uid)
                    .get()
                    .await()
            if (userDoc.exists()) {
                val user = userDoc.toObject(User::class.java)
                user?.let{u->
                    if(u.profilePictureUrl.isNullOrEmpty()){
                        firebaseUser.photoUrl?.let{photoUrl->
                           val updatedUser = u.copy(profilePictureUrl = photoUrl.toString())
                            firestore
                                .collection("USERS")
                                .document(firebaseUser.uid)
                                .update("profilePictureUrl", photoUrl.toString())
                                .await()
                            updatedUser
                        }?:u
                    }else{
                        u
                    }

                }
            } else {
                User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    fullName = firebaseUser.displayName ?: "",
                    phoneNumber = firebaseUser.phoneNumber ?: "",
                    profilePictureUrl = firebaseUser.photoUrl?.toString(),
                    createdAt = System.currentTimeMillis()
                ).also{newUser->
                        firestore
                            .collection("USERS")
                            .document(firebaseUser.uid)
                            .set(newUser)
                            .await()
                }
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error getting current user: ${e.message}")
            null
        }
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val result = auth.signInWithCredential(credential).await()

                val firebaseUser = result.user
                    ?: return@withContext Result.Error("Authentication failed - no user returned")

                // Check if user exists in Firestore
                val userDoc = firestore.collection("USERS")
                    .document(firebaseUser.uid)
                    .get()
                    .await()

                val finalUser = if (userDoc.exists()) {
                    userDoc.toObject(User::class.java)!!
                }else{
                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = account.email ?: firebaseUser.email ?: "",
                        fullName = account.displayName ?: firebaseUser.displayName ?: "Google User",
                        phoneNumber = "",
                        profilePictureUrl = account.photoUrl?.toString() ?: firebaseUser.photoUrl?.toString(),
                        createdAt = System.currentTimeMillis()
                    )
                    firestore.collection("USERS")
                        .document(firebaseUser.uid)
                        .set(newUser)
                        .await()
                    newUser
                }
                Result.Success(finalUser)
            }catch (e: Exception) {
                Result.Error(e.localizedMessage?:"Google sign-in failed")


            }

        }

}
