package com.example.childcontrol.applist


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.childcontrol.headchild.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AppListViewModel(private val repository: AppListRepository) :
    ViewModel() {

    // LiveData для хранения списка приложений
    private var _appList = MutableLiveData<List<AppInfo>?>()
    val appList: LiveData<List<AppInfo>?> get() = _appList

    // Функция для получения списка приложений из репозитория
    fun getAppList() {
        viewModelScope.launch(Dispatchers.Main) {
            repository.getAppList{
            _appList.value = it
            }
        }
    }

    // Функция для установки заблокированных приложений в репозитории
    fun setLockApps(appID: Int, lockApp: LockApp) {
        // Запуск корутины для выполнения операции ввода-вывода
        viewModelScope.launch(Dispatchers.Main) {
            repository.setLockApps(appID, lockApp)
        }
    }
}
