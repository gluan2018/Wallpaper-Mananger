package br.com.yuki.wallpaper.manager.screen.home.fragment.alarm.dialog.adapter

import android.view.View
import android.view.ViewGroup
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.database.model.image.relation.ImageAlbum
import br.com.yuki.wallpaper.manager.databinding.CardAlarmAlbumBinding
import br.com.yuki.wallpaper.manager.util.base.adapter.BaseAdapter
import br.com.yuki.wallpaper.manager.util.global.utility.inflater
import br.com.yuki.wallpaper.manager.util.global.utility.pixel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class AdapterAlarmAlbum : BaseAdapter<ImageAlbum, AdapterAlarmAlbum.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        CardAlarmAlbumBinding.inflate(parent.inflater, parent, false).root
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        with(holder.binding) {
            val item = get(position)

            Glide.with(root.context.applicationContext)
                .load(item.firstImage?.path)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(10f.pixel)))
                .fallback(R.drawable.box)
                .into(Image)
                .clearOnDetach()

            root.setOnClickListener {
                callOnClick(item)
            }
        }
    }

    class Holder(view: View) : BaseAdapter.Holder(view) {
        val binding = CardAlarmAlbumBinding.bind(view)
    }

}