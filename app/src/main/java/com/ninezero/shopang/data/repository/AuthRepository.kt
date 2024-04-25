package com.ninezero.shopang.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ninezero.shopang.R
import com.ninezero.shopang.util.AuthState
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.USER_COLLECTION
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.tasks.await
import java.lang.Exception
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
}