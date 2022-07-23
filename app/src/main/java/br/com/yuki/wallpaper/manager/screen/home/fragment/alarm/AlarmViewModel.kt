package br.com.yuki.wallpaper.manager.screen.home.fragment.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.yuki.wallpaper.manager.database.service.AlarmInterface
import br.com.yuki.wallpaper.manager.model.Alarm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmViewModel constructor(
    private val alarmInterface: AlarmInterface
) : ViewModel() {

    fun getAll(): LiveData<List<Alarm>> {
        val liveData = MutableLiveData<List<Alarm>>()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    val all = alarmInterface.getAll()
                    liveData.postValue(all)
                }
            }
        }

        return liveData
    }

    fun add(alarm: Alarm): LiveData<Int> {
        val liveData = MutableLiveData<Int>()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    alarmInterface.insert(alarm)
                }.onSuccess {
                    liveData.postValue(it.toInt())
                }
            }
        }

        return liveData
    }

    fun update(alarm: Alarm): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    alarmInterface.update(alarm)
                }.onSuccess {
                    liveData.postValue(true)
                }
            }
        }

        return liveData
    }

    fun cancel(alarm: Alarm): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                runCatching {
                    alarmInterface.delete(alarm)
                }.onSuccess {
                    liveData.postValue(true)
                }
            }
        }

        return liveData
    }

}