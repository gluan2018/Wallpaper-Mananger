package br.com.yuki.wallpaper.manager.screen.presenting.dialog.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.databinding.CardInfoAddBinding
import br.com.yuki.wallpaper.manager.util.base.adapter.BaseAdapter
import br.com.yuki.wallpaper.manager.util.global.utility.inflater
import br.com.yuki.wallpaper.manager.util.global.utility.pixel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class InfoAddAdapter : RecyclerView.Adapter<BaseAdapter.Holder>() {

    private val items:MutableList<Image> = mutableListOf()

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter.Holder = BaseAdapter.Holder(CardInfoAddBinding.inflate(parent.inflater, parent, false).root)

    override fun onBindViewHolder(holder: BaseAdapter.Holder, position: Int) {
        val binding = CardInfoAddBinding.bind(holder.itemView)

        val item = items[position]
        Glide.with(binding.root.context.applicationContext)
            .load(item.path)
            .transition(DrawableTransitionOptions.withCrossFade())
            .transform(MultiTransformation(CenterCrop(), RoundedCorners(10f.pixel)))
            .into(binding.imageInfo)
            .clearOnDetach()
    }

    fun add(item: Image) {
        if (itemCount >= 3) {
            items.removeAt(0)
            notifyItemRemoved(0)
        }

        items.add(item)
        notifyItemInserted(itemCount - 1)
    }

}