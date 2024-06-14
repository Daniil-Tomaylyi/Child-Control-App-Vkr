package com.example.childcontrol.applist


import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.childcontrol.headchild.AppInfo
import com.example.childcontrol.databinding.ItemAppListBinding

class AppListAdapter(private val appListViewModel: AppListViewModel) :
    RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    // Данные для списка приложений
    var data: List<AppInfo> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged() // Уведомление об изменении данных
        }

    // ViewHolder для элемента списка приложений
    class AppListViewHolder(val binding: ItemAppListBinding) : RecyclerView.ViewHolder(binding.root)

    // Создание нового ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAppListBinding.inflate(inflater, parent, false)
        return AppListViewHolder(binding)
    }

    // Получение количества элементов в данных
    override fun getItemCount(): Int = data.size

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val applist = data[position]
        with(holder.binding) {
            nameApp.text = applist.name // Установка имени приложения
            // Декодирование и установка иконки приложения
            val bytes = Base64.decode(applist.icon, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            iconApp.setImageDrawable(BitmapDrawable(Resources.getSystem(), bitmap))
            // Установка слушателя изменения состояния переключателя блокировки приложения
            lockAppSwitch.setOnCheckedChangeListener { _, isChecked ->
                // Обновление статуса блокировки в Firebase через ViewModel
                appListViewModel.setLockApps(position, LockApp(applist.packageName, isChecked))
            }
        }
    }
}



