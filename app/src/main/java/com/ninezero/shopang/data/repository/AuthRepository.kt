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

    private val userUid by lazy { fAuth.uid!! }
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
        fUserCollection
            .document(userUid)
            .addSnapshotListener { value, _ ->
                if (value != null && value.exists()) {
                    val userInfo = mapToUserInfo(value.data!!)
                    userInfoLiveData.postValue(ResponseWrapper.Success(userInfo))
                } else {
                    userInfoLiveData.postValue(ResponseWrapper.Error(context.getString(R.string.error_msg)))
                }
            }
    }

    suspend fun uploadUserInfo(
        platform: String,
        userName: String,
        userAddress: String?,
        profileImageUri: Uri?,
        isUpdate: Boolean
    ): ResponseWrapper<String> {
        return try {
            if (isUpdate) {
                if (profileImageUri != null) {
                    val imageUrl = uploadProfileImage(profileImageUri)
                    val userInfo = UserInfo(userUid, platform, userName, userAddress, imageUrl)
                    fUserCollection.document(userUid).update(userInfo.toMap()).await()
                } else {
                    val userInfo = UserInfo(userUid, platform, userName, userAddress)
                    fUserCollection.document(userUid).update(userInfo.toMap()).await()
                }
                Log.d("uploadUserInfo", "계정 업데이트 성공")
                ResponseWrapper.Success(context.getString(R.string.success_updated_account))
            } else {
                val userInfo = UserInfo(userUid, platform, userName, userAddress)
                fUserCollection.document(userUid).set(userInfo.toMap()).await()
                Log.d("uploadUserInfo", "계정 생성 성공")
                ResponseWrapper.Success(context.getString(R.string.success_create_account))
            }
        } catch (e: Exception) {
            ResponseWrapper.Error(context.getString(R.string.error_create_account))
        }
    }

    private suspend fun uploadProfileImage(profileImageUri: Uri): String {
        val fileName = "${USER_COLLECTION}/${System.currentTimeMillis()}.jpg"
        val task = fStorage.reference.child(fileName).putFile(profileImageUri)
        val result = task.await()
        return result.storage.downloadUrl.await().toString()
    }
}