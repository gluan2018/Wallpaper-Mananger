package br.com.yuki.wallpaper.manager.screen.home.fragment.settings

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.databinding.CardWeekDayBinding
import br.com.yuki.wallpaper.manager.util.global.OnClickListener
import br.com.yuki.wallpaper.manager.util.global.utility.inflater

class AdapterWeekDay(
    context: Context,
    var weekDay: Int
) : RecyclerView.Adapter<AdapterWeekDay.Holder>() {

    private val days: Array<String> = context.resources.getStringArray(R.array.week_days)

    private var onCheckedListener: OnClickListener<Int>? = null

    override fun getItemCount(): Int = 7

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        CardWeekDayBinding.inflate(parent.inflater, parent, false).root
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.root.textOn = days[position]
        holder.binding.root.textOff = days[position]

        holder.binding.root.setOnCheckedChangeListener(null)
        holder.binding.root.isChecked = ((weekDay shl position) and 1) == 1

        holder.binding.root.setOnCheckedChangeListener { _, isChecked ->
            weekDay = if (isChecked)
                weekDay or (1 shr position)
            else
                weekDay and (0 shr position)
            onCheckedListener?.onClick(weekDay)
        }
    }

    fun setOnCheckChangeListener(callback: OnClickListener<Int>) {
        onCheckedListener = callback
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: CardWeekDayBinding = CardWeekDayBinding.bind(view)
    }
}