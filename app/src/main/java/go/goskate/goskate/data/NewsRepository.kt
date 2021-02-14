package go.goskate.goskate.data


import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import go.goskate.goskate.vo.PostVO
import java.util.*
import kotlin.collections.HashMap

class NewsRepository {


    var storage: FirebaseStorage = FirebaseStorage.getInstance()

    fun savePost(postVO: PostVO): MutableLiveData<String> {
        val result = MutableLiveData<String>()
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("News")
        val refStorage = storage.getReference("imagesNews/" + UUID.randomUUID().toString())
        refStorage.putFile(postVO.fileImageCapture!!.toUri()).addOnSuccessListener {
            if (it.task.isSuccessful) {
                postVO.fileImageCapture = it.task.result.toString()
                val userMap = HashMap<String, Any>()
                userMap["fileImageCapture"] = postVO.fileImageCapture!!
                userMap["typeCapture"] = postVO.typeCapture!!
                userMap["location"] = postVO.location
                userMap["description"] = postVO.description

                userRef.setValue(userMap)
                    .addOnCompleteListener { task ->
                        result.value = if (task.isSuccessful) {
                            "Successful"
                        } else {
                            task.exception?.toString()!!
                        }
                    }
            }
        }

        return result
    }

    fun getAllPost(): MutableLiveData<List<PostVO>> {
        val resultPost = MutableLiveData<List<PostVO>>()
        val postVO = PostVO()
        val data = HashMap<String, PostVO>()
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("News")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val mutable = mutableListOf<PostVO>()
                    val userMap = HashMap<String, Any>()
                    userMap["typeCapture"] to postVO.typeCapture!!
                    userMap["location"] to postVO.location
                    userMap["description"] to postVO.description
                    mutable.add(PostVO(snapshot.value.toString()))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return resultPost
    }


}
