package br.com.yuki.wallpaper.manager.screen.home.fragment.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.util.tools.Preferences
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var observerImage: Preferences<Image>? = null

    fun getCurrent() = liveData {
        emit(observerImage?.current)
    }

    fun listen() = liveData {
        observerImage?.removeObserver()
        observerImage = Preferences.image(getApplication())
        observerImage?.removeObserver()
        observerImage?.setCallback { newImage ->
            viewModelScope.launch {
                emit(newImage)
            }
        }
    }

    override fun onCleared() {
        observerImage?.removeObserver()
        super.onCleared()
    }

}