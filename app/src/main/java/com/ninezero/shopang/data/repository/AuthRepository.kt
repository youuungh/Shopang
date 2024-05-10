package com.ninezero.shopang.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ninezero.shopang.R
import com.ninezero.shopang.model.UserInfo
import com.ninezero.shopang.model.mapToUserInfo
import com.ninezero.shopang.util.AuthState
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.USER_COLLECTION
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@ViewModelScoped
class AuthRepository @Inject constructor(
    private val fAuth: FirebaseAuth,
    private val fStore: FirebaseFirestore,
    private val fStorage: FirebaseStorage,
    @ApplicationContext private val context: Context
) {

    private val userUid: String?
        get() = fAuth.uid

    private val fUserCollection by lazy { fStore.collection(USER_COLLECTION) }

    init {
        fAuth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
    }

    fun isUserLoggedIn(): Boolean = fAuth.currentUser != null

    fun authCallBack(authLiveData: MutableLiveData<AuthState>): PhoneAuthProvider.OnVerificationStateChangedCallbacks {
        return object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                authLiveData.value = AuthState.SuccessWithCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                authLiveData.value = AuthState.Error(e)
            }

            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                authLiveData.value = AuthState.SuccessWithCode(id, token)
            }
        }
    }

    suspend fun signInWithCredential(credential: AuthCredential): ResponseWrapper<Unit?> {
        return try {
            fAuth.signInWithCredential(credential).await()
            ResponseWrapper.Success(null)
        } catch (e: Exception) {
            ResponseWrapper.Error(context.getString(R.string.error_msg))
        }
    }

    fun getUserInfo(userInfoLiveData: MutableLiveData<ResponseWrapper<UserInfo>>) {
        userUid?.let {
            fUserCollection
                .document(userUid!!)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val userInfo = mapToUserInfo(document.data!!)
                        Log.d("AuthRepository", "getUserInfo 성공")
                        userInfoLiveData.postValue(ResponseWrapper.Success(userInfo))
                    } else {
                        Log.d("AuthRepository", "getUserInfo 실패")
                        userInfoLiveData.postValue(ResponseWrapper.Error(context.getString(R.string.error_msg)))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("AuthRepository", "getUserInfo 실패: ${exception.message}")
                    userInfoLiveData.postValue(ResponseWrapper.Error(context.getString(R.string.error_msg)))
                }
        }
    }

    fun getUserInfoRealTime(userInfoLiveData: MutableLiveData<ResponseWrapper<UserInfo>>) {
        userUid?.let {
            fUserCollection
                .document(userUid!!)
                .addSnapshotListener { value, _ ->
                    if (value != null && value.exists()) {
                        val userInfo = mapToUserInfo(value.data!!)
                        Log.d("AuthRepository", "getUserInfoRealTime 성공")
                        userInfoLiveData.postValue(ResponseWrapper.Success(userInfo))
                    } else {
                        Log.d("AuthRepository", "getUserInfoRealTime 실패")
                        userInfoLiveData.postValue(ResponseWrapper.Error(context.getString(R.string.error_msg)))
                    }
                }
        }
    }

    suspend fun uploadUserInfo(
        platform: String,
        userName: String,
        userAddress: String?,
        profileImageUri: Uri?,
        isUpload: Boolean,
        isUpdate: Boolean
    ): ResponseWrapper<String> {
        return try {
            val imageUrl = if (isUpload && profileImageUri != null) {
                uploadProfileImage(profileImageUri)
            } else {
                profileImageUri?.toString()
            }

            val userInfo = UserInfo(userUid!!, platform, userName, userAddress, imageUrl)

            if (isUpdate) {
                fUserCollection.document(userUid!!).update(userInfo.toMap()).await()
                Log.d("uploadUserInfo", "계정 업데이트 성공")
                ResponseWrapper.Success(context.getString(R.string.success_updated_account))
            } else {
                fUserCollection.document(userUid!!).set(userInfo.toMap()).await()
                Log.d("uploadUserInfo", "계정 생성 성공")
                ResponseWrapper.Success(context.getString(R.string.success_create_account))
            }
        } catch (e: Exception) {
            ResponseWrapper.Error(context.getString(R.string.error_create_account))
        }
    }

    suspend fun updateUserName(userName: String): ResponseWrapper<Unit?> {
        return try {
            fUserCollection.document(userUid!!).update(mapOf("userName" to userName)).await()
            ResponseWrapper.Success(null)
        } catch (e: Exception) {
            ResponseWrapper.Error(context.getString(R.string.error_msg))
        }
    }

    suspend fun updateUserAddress(userAddress: String): ResponseWrapper<Unit?> {
        return try {
            fUserCollection.document(userUid!!).update(mapOf("userAddress" to userAddress)).await()
            ResponseWrapper.Success(null)
        } catch (e: Exception) {
            ResponseWrapper.Error(context.getString(R.string.error_msg))
        }
    }

    private suspend fun uploadProfileImage(profileImageUri: Uri): String {
        val fileName = "${USER_COLLECTION}/$userUid.jpg"
        val inputStream = context.contentResolver.openInputStream(profileImageUri)
        val reference = fStorage.reference.child(fileName)

        try {
            reference.delete().await()
        } catch (e: Exception) {
            Log.d("uploadProfileImage", "기존 이미지 없음")
        }

        val task = reference.putStream(inputStream!!)
        val result = task.await()
        inputStream.close()
        return result.storage.downloadUrl.await().toString()
    }
}