package br.com.yuki.wallpaper.manager.util.base.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import br.com.yuki.wallpaper.manager.util.global.OnClickListener

abstract class BaseAdapter<D, H : RecyclerView.ViewHolder> : RecyclerView.Adapter<H>() {

    protected val items: MutableList<D> = mutableListOf()

    private var onClick: OnClickListener<D>? = null

    override fun getItemCount(): Int = items.size

    operator fun get(position: Int) = items[position]

    fun getOrNull(position: Int) = items.getOrNull(position)

    fun all() = items.toList()

    open fun add(item: D) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    open fun add(items: Collection<D>) {
        this.items.addAll(items)
        notifyItemRangeInserted(this.items.size - items.size, items.size)
    }

    open fun remove(item: D) {
        val index = items.indexOf(item).takeIf { it >= 0 } ?: return
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    open fun edit(item: D) {
        val index = items.indexOf(item).takeIf { it >= 0 } ?: return
        items[index] = item
        notifyItemChanged(index)
    }

    open fun setOnClickListener(listener: OnClickListener<D>?) {
        onClick = listener
    }

    open fun set(items: Collection<D>) {
        if (items.size < this.items.size) {
            for (index in 0..this.items.size) {
                if (index >= items.size) {
                    this.items.removeAt(index)
                    notifyItemRemoved(index)
                } else {
                    this.items[index] = items.toList()[index]
                    notifyItemChanged(index)
                }
            }
            return
        }
        items.forEachIndexed { index, item ->
            if (index < this.items.size) {
                this.items[index] = item
                notifyItemChanged(index)
            } else {
                this.items.add(item)
                notifyItemInserted(index)
            }
        }
    }

    open fun clear() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    protected fun callOnClick(item: D) {
        onClick?.onClick(item)
    }

    open class Holder(view: View) : RecyclerView.ViewHolder(view)

}