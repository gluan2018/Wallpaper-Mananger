package br.com.yuki.wallpaper.manager.util.base.factory

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.com.yuki.wallpaper.manager.database.manager.AppDatabase
import br.com.yuki.wallpaper.manager.database.service.AlarmInterface
import br.com.yuki.wallpaper.manager.database.service.WallpaperService

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.wallpaperViewModels(): Lazy<VM> {
    val factoryPromise = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return modelClass.getConstructor(WallpaperService::class.java)
                    .newInstance(AppDatabase.getInstance(applicationContext).wallpaper())
            }

            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return modelClass.getConstructor(WallpaperService::class.java)
                    .newInstance(AppDatabase.getInstance(applicationContext).wallpaper())
            }
        }
    }

    return ViewModelLazy(VM::class, { viewModelStore }, factoryPromise)
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.wallpaperViewModels(): Lazy<VM> {
    val factoryPromise = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return modelClass.getConstructor(WallpaperService::class.java)
                    .newInstance(AppDatabase.getInstance(requireActivity().applicationContext).wallpaper())
            }
        }
    }

    return ViewModelLazy(VM::class, { requireActivity().viewModelStore }, factoryPromise)
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.alarmViewModels(): Lazy<VM> {
    val factoryPromise = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return modelClass.getConstructor(AlarmInterface::class.java)
                    .newInstance(AppDatabase.getInstance(requireActivity().applicationContext).alarm())
            }
        }
    }

    return ViewModelLazy(VM::class, { requireActivity().viewModelStore }, factoryPromise)
}