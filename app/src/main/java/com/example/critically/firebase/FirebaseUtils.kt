package com.example.critically.firebase

import android.util.Log
import com.example.critically.navigation.PostOfficeAppRouter
import com.example.critically.navigation.Screen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseUtils() {

    companion object {
        private val firestore: FirebaseFirestore
            get() = FirebaseFirestore.getInstance()

        private const val TAG = "Firestore"

        fun checkIfUserExists(username: String): Boolean {
            Log.d(TAG, "checkIfUserExists: $username")
            var result = false
            firestore
                .collection("users")
                .get()
                .addOnSuccessListener {
                    firestore.collection("users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                result = false
                            } else {
                                result = true
                                deleteAuthUser()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Erro ao buscar o documento", e)
                            result = false
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "createUserInFirebase: Error adding document", e)
                    result = false
                    FirebaseCrashlytics.getInstance().recordException(e)
                    FirebaseCrashlytics.getInstance().sendUnsentReports()
                    e.printStackTrace()
                }
            return result
        }

        fun createUserInFirestore(userId: String?, username: String, name: String, email: String) {
            Log.d(TAG, "createUserInFirestore: {$userId, $username, $name, $email}")
            val user = hashMapOf(
                "uid" to userId,
                "name" to name,
                "username" to username,
                "email" to email
            )

            userId?.let {
                firestore.collection("users")
                    .document(it)
                    .set(user)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Usuário criado com sucesso!")
                        PostOfficeAppRouter.navigateTo(Screen.BottomNavigation)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Erro ao criar usuário: ${exception.message}")
                        FirebaseCrashlytics.getInstance().recordException(exception)
                        FirebaseCrashlytics.getInstance().sendUnsentReports()
                        exception.printStackTrace()
                    }
            }
        }

        fun createGoogleUserInFirestore(user: FirebaseUser?) {
            Log.d(TAG, "createGoogleUserInFirestore")
            if (user != null) {
                val userRef = FirebaseFirestore.getInstance().collection("users").document(user.uid)

                userRef.get().addOnSuccessListener { result ->
                    if (result.exists()) {
                        Log.d(TAG, "Usuário já existe no Firestore.")
                        return@addOnSuccessListener
                    }
                    val userData = hashMapOf(
                        "name" to user.displayName,
                        "email" to user.email,
                        "profile_picture" to user.photoUrl,
                        "username" to "@${user.email!!.split("@")[0]}"
                    )

                    userRef.set(userData)
                        .addOnSuccessListener {
                            Log.d(TAG, "createGoogleUserInFirestore: Usuário criado com sucesso!")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "createGoogleUserInFirestore: Erro ao criar usuário: ${e.message}")
                            FirebaseCrashlytics.getInstance().recordException(e)
                            FirebaseCrashlytics.getInstance().sendUnsentReports()
                            e.printStackTrace()
                        }
                }.addOnFailureListener { e ->
                    Log.e(TAG, "createGoogleUserInFirestore: Erro ao verificar usuário: ${e.message}")
                    FirebaseCrashlytics.getInstance().recordException(e)
                    FirebaseCrashlytics.getInstance().sendUnsentReports()
                    e.printStackTrace()
                }
            } else {
                Log.w("Auth", "Nenhum usuário autenticado encontrado.")
            }
        }

        private fun deleteAuthUser() {
            val user = Firebase.auth.currentUser
            user?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firestore", "Usuário deletado com sucesso!")
                    }
                }
        }
    }
}