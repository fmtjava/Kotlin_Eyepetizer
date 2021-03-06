package com.fmt.kotlin.eyepetizer.common.base.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.lang.Exception

open class BaseViewModel : ViewModel() {

    val mStateLiveData = MutableLiveData<State>()

    fun <T> liveDataEx(block: suspend () -> T) = liveData {
        try {
            mStateLiveData.value = LoadState
            emit(block())
            mStateLiveData.value = SuccessState
        } catch (e: Exception) {
            mStateLiveData.value = ErrorState(e.message)
        }
    }

    fun <T> flowEx(block: suspend () -> T) = flow {
        emit(block())
    }.onStart {
        mStateLiveData.value = LoadState
    }.onCompletion {
        mStateLiveData.value = SuccessState
    }.catch { cause ->
        mStateLiveData.value = ErrorState(cause.message)
    }.asLiveData()
}

sealed class State

object LoadState : State()

object SuccessState : State()

class ErrorState(val errorMsg: String?) : State()
