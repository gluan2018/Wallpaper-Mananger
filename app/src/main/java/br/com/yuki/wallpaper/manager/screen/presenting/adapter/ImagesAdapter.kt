package br.com.yuki.wallpaper.manager.screen.presenting.adapter

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.databinding.CardImageBinding
import br.com.yuki.wallpaper.manager.util.base.adapter.BaseAdapter
import br.com.yuki.wallpaper.manager.util.global.utility.inflater
import br.com.yuki.wallpaper.manager.util.global.utility.pixel
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.util.ViewPreloadSizeProvider

class ImagesAdapter(
    val albumId: Long
) : BaseAdapter<Image, ImagesAdapter.ImagesAlbumHolder>() {

    fun interface OnMove {
        fun onMove(from: Image, to: Image)
    }

    private var onMoveListener: OnMove? = null

    var currentIndex = -1
    var currentSelected: Image? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesAlbumHolder = ImagesAlbumHolder(
        CardImageBinding.inflate(parent.inflater, parent, false).root
    )

    override fun onViewRecycled(holder: ImagesAlbumHolder) {
        Glide.with(holder.itemView.context.applicationContext)
            .clear(holder.binding.Image)
    }

    override fun onBindViewHolder(holder: ImagesAlbumHolder, position: Int) {
        with(holder.binding) {
            val item = get(position)
            Glide.with(root.context.applicationContext)
                .load(item.path)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(10f.pixel)))
                .into(Image)
                .clearOnDetach()

            if (position == 0 && currentSelected == null) {
                currentSelected = item
                currentIndex = 0
            }

            if (currentSelected == item)
                Image.setBackgroundResource(R.drawable.background_image_select)
            else
                Image.background = null

            root.setOnClickListener {
                if (item != currentSelected) {
                    val index = currentSelected?.run {
                        val indexOf = super.items.indexOf(this)
                        if (indexOf >= 0)
                            return@run indexOf
                        return@run null
                    }
                    currentIndex = position
                    currentSelected = item

                    if (index != null)
                        notifyItemChanged(index)
                    notifyItemChanged(position)

                    callOnClick(item)
                }
            }
        }
    }

    fun attach(
        recyclerView: RecyclerView,
        orientation: Int
    ) {
        val loader = RecyclerViewPreloader(
            Glide.with(
                recyclerView.context.applicationContext
            ),
            PreloadModelProvider(
                recyclerView.context.applicationContext
            ),
            ViewPreloadSizeProvider(),
            50
        )

        ItemTouchHelper(
            DragDrop(
                if (orientation == Configuration.ORIENTATION_PORTRAIT)
                    ItemTouchHelper.START or ItemTouchHelper.END
                else
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN
            )
        ).attachToRecyclerView(recyclerView)

        recyclerView.adapter = this
        recyclerView.addOnScrollListener(loader)
    }

    override fun remove(item: Image) {
        if (currentSelected == item)
            currentSelected = null
        super.remove(item)
    }

    fun setOnMoveListener(callback: OnMove?) {
        onMoveListener = callback
    }

    fun selectLast(): Image? {
        when {
            currentIndex == 0 && itemCount > 0 -> {
                currentSelected = get(currentIndex)
                notifyItemChanged(currentIndex)
            }
            currentIndex > 0 && itemCount > 0 -> {
                currentSelected = get(--currentIndex)
                notifyItemChanged(currentIndex)
            }
            else -> {
                currentIndex = -1
                currentSelected = null
            }
        }
        return currentSelected
    }

    class ImagesAlbumHolder(view: View) : BaseAdapter.Holder(view) {
        val binding = CardImageBinding.bind(view)
    }

    private inner class PreloadModelProvider(
        private val context: Context
    ) : ListPreloader.PreloadModelProvider<String> {
        override fun getPreloadItems(position: Int): MutableList<String> {
            val link = this@ImagesAdapter.getOrNull(position)?.path ?: return mutableListOf()
            return mutableListOf(link)
        }

        override fun getPreloadRequestBuilder(item: String): RequestBuilder<*> {
            return Glide.with(context.applicationContext)
                .load(item)
                .transform(MultiTransformation(CenterCrop(), RoundedCorners(8f.pixel)))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
        }
    }

    private inner class DragDrop(
        dragsDirs: Int
    ) : ItemTouchHelper.SimpleCallback(
        dragsDirs, 0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val (from, to) = getOrNull(viewHolder.adapterPosition) to getOrNull(target.adapterPosition)
            if (from != null && to != null) {

                from.index = target.adapterPosition
                to.index = viewHolder.adapterPosition

                items[viewHolder.adapterPosition] = to
                items[target.adapterPosition] = from

                currentSelected?.apply {
                    currentIndex = items.indexOf(this)
                }

                onMoveListener?.onMove(from, to)
            }
            notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit
    }

}