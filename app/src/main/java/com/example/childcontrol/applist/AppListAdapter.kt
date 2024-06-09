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

    var data: List<AppInfo> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    class AppListViewHolder(val binding: ItemAppListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAppListBinding.inflate(inflater, parent, false)
        return AppListViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        val applist = data[position]
        val context = holder.itemView.context
        with(holder.binding) {
            nameApp.text = applist.name
            val bytes = Base64.decode(applist.icon, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            iconApp.setImageDrawable(BitmapDrawable(Resources.getSystem(), bitmap))
            lockAppSwitch.setOnCheckedChangeListener { _, isChecked ->
                // Обновите статус блокировки в Firebase через ViewModel
                appListViewModel.setLockApps(position, lockApp(applist.packageName, isChecked))
            }
        }
    }

}


