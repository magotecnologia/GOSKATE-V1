package go.goskate.goskate.data

import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import go.goskate.goskate.vo.UserVO
import java.util.*

class AuthRepository() {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun saveUser(newUser: UserVO): MutableLiveData<String> {
        val mutableDataResponse = MutableLiveData<String>()
        auth.createUserWithEmailAndPassword(newUser.userEmail, newUser.userEmail)
            .addOnCompleteListener { task ->
                var message = task.exception?.toString()
                if (task.isSuccessful) {
                    newUser.userId = auth.currentUser!!.uid
                    val userRef: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("Users")
                    val refStorage =
                        storage.getReference("imagesProfile/" + UUID.randomUUID().toString())
                    refStorage.putFile(newUser.profileImage!!.toUri()).addOnSuccessListener {
                        if (it.task.isSuccessful) {
                            newUser.profileImage = it.task.result.toString()
                            val userMap = HashMap<String, Any>()
                            userMap["uid"] = newUser.userId
                            userMap["imageProfile"] = newUser.profileImage!!
                            userMap["userName"] = newUser.userName
                            userMap["userEmail"] = newUser.userEmail
                            userMap["userPassword"] = newUser.userPassword
                            userMap["userTelephone"] = newUser.userTelephone
                            userMap["ageUser"] = newUser.userAge
                            userMap["sexUser"] = newUser.userGender!!

                            userRef.child(newUser.userId).setValue(userMap)
                                .addOnCompleteListener { task ->
                                    message = task.exception?.toString()
                                    mutableDataResponse.value = if (task.isSuccessful) {
                                        "Successful"
                                    } else {
                                        "$message"
                                    }
                                }
                        } else {
                            message = it.task.exception?.toString()
                        }
                    }

                } else {
                    mutableDataResponse.value = message
                }

            }
        return mutableDataResponse
    }

    fun isUserExist(user: UserVO): MutableLiveData<String> {
        val mutableDataLoginResponse = MutableLiveData<String>()
        auth.signInWithEmailAndPassword(user.userEmail, user.userPassword)
            .addOnCompleteListener { task ->
                val message = task.exception.toString()
                mutableDataLoginResponse.value = if (task.isSuccessful) {
                    "Successful"
                } else {
                    message
                }
            }
        return mutableDataLoginResponse
    }

    fun signInWithCredential(account: GoogleSignInAccount?): MutableLiveData<String> {
        val mutableDataLoginWithGoogleResponse = MutableLiveData<String>()
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            val message = task.exception.toString()
            mutableDataLoginWithGoogleResponse.value = if (task.isSuccessful) {
                "Successful"
            } else {
                message
            }
        }
        return mutableDataLoginWithGoogleResponse
    }


    fun recoverAccountWhitEmail(email: String): MutableLiveData<String> {
        val mutableRecoverAccountWhitEmail = MutableLiveData<String>()
        auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            val message = task.exception.toString()
            mutableRecoverAccountWhitEmail.value = if (task.isSuccessful) {
                "Por favor revisa tu correo para restablecer la contrase;a"
            } else {
                message
            }
        }

        return mutableRecoverAccountWhitEmail
    }

}