package br.com.yuki.wallpaper.manager.util.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.yuki.wallpaper.manager.util.base.model.StringFormatter

abstract class ViewModelLiveData : ViewModel() {

    private val errorObserver: MutableLiveData<StringFormatter> = MutableLiveData()

    val error: LiveData<StringFormatter>
        get() = errorObserver

    fun sendError(error: StringFormatter) = errorObserver.postValue(error)

    fun Result<*>.callOnError() {
        onFailure { error ->
            error.printStackTrace()
            errorObserver.postValue(StringFormatter(throwable = error))
        }
    }

    suspend fun <T> Result<T>.callOnSuccessful(liveDataScope: LiveDataScope<T>) {
        onSuccess { value ->
            liveDataScope.emit(value)
        }
    }

    suspend fun <T> Result<T>.emit(liveDataScope: LiveDataScope<T>) {
        callOnSuccessful(liveDataScope)
        callOnError()
    }

}