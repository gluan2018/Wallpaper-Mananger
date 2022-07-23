package br.com.yuki.wallpaper.manager.screen.home.fragment.settings

import android.view.View
import android.view.ViewGroup
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.databinding.CardAlarmBinding
import br.com.yuki.wallpaper.manager.model.Alarm
import br.com.yuki.wallpaper.manager.util.base.adapter.BaseAdapter
import br.com.yuki.wallpaper.manager.util.global.utility.inflater
import com.google.android.material.color.MaterialColors

class AdapterAlarm : BaseAdapter<Alarm, AdapterAlarm.Holder>() {

    fun interface OnUpdateListener {

        enum class State {
            PICKER, DELETE, UPDATE, ALBUM
        }

        fun update(alarm: Alarm, state: State)
    }

    private var onUpdateListener: OnUpdateListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        CardAlarmBinding.inflate(parent.inflater, parent, false).root
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = get(position)

        holder.binding.containerWeekDay.adapter = AdapterWeekDay(holder.itemView.context, item.days)

        holder.binding.IsEnable.isOn = item.isEnable
        holder.binding.TimeAlarm.text = String.format("%02d:%02d", item.hour, item.minute)
        holder.binding.AlbumId.text = item.albumName ?: holder.itemView.context.getString(R.string.select_album)

        holder.binding.IsEnable.colorOff = MaterialColors.getColor(holder.itemView, R.attr.colorOnSurface)
        holder.binding.IsEnable.colorOn = MaterialColors.getColor(holder.itemView, R.attr.colorSecondaryContainer)
        holder.binding.IsEnable.colorBorder = MaterialColors.getColor(holder.itemView, R.attr.colorSecondaryContainer)

        holder.binding.IsEnable.setOnToggledListener { _, isOn ->
            item.isEnable = isOn
            onUpdateListener?.update(item, OnUpdateListener.State.UPDATE)
        }
        holder.binding.TimeAlarm.setOnClickListener {
            onUpdateListener?.update(item, OnUpdateListener.State.PICKER)
        }
        holder.binding.selectAlbum.setOnClickListener {
            onUpdateListener?.update(item, OnUpdateListener.State.ALBUM)
        }
        holder.binding.deleteAlarm.setOnClickListener {
            onUpdateListener?.update(item, OnUpdateListener.State.DELETE)
        }
    }

    fun setOnUpdateListener(callback: OnUpdateListener?) {
        onUpdateListener = callback
    }

    class Holder(view: View) : BaseAdapter.Holder(view) {
        val binding: CardAlarmBinding = CardAlarmBinding.bind(view)
    }

}