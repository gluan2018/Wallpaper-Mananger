package br.com.yuki.wallpaper.manager.screen.home.fragment.album.adapter

import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.database.model.image.relation.ImageAlbum
import br.com.yuki.wallpaper.manager.databinding.CardAlbumBinding
import br.com.yuki.wallpaper.manager.util.base.adapter.BaseAdapter
import br.com.yuki.wallpaper.manager.util.global.GlideApp
import br.com.yuki.wallpaper.manager.util.global.OnClickListener
import br.com.yuki.wallpaper.manager.util.global.utility.inflater
import br.com.yuki.wallpaper.manager.util.global.utility.pixel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class AlbumsAdapter : BaseAdapter<ImageAlbum, BaseAdapter.Holder>() {

    private val transform = MultiTransformation(
        CenterCrop(),
        RoundedCorners(10f.pixel)
    )

    private var onEditListener: OnClickListener<ImageAlbum>? = null
    private var onApplyListener: OnClickListener<ImageAlbum>? = null
    private var onDeleteListener: OnClickListener<ImageAlbum>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        CardAlbumBinding.inflate(parent.inflater, parent, false).root
    )

    override fun onViewRecycled(holder: Holder) {
        with(CardAlbumBinding.bind(holder.itemView)) {
            Glide.with(root.context.applicationContext)
                .clear(ImageAlbum)
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        with(CardAlbumBinding.bind(holder.itemView)) {
            val item = get(position)

            currentNameAlbum.setText(item.album.name)

            currentNameAlbum.setOnEditorActionListener { view, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    view?.apply {
                        clearFocus()
                        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
                    }
                    item.album.name = view?.text?.toString() ?: ""

                    onEditListener?.onClick(item)
                    return@setOnEditorActionListener false
                }
                return@setOnEditorActionListener false
            }

            if (item.firstImage == null)
                ImageAlbum.scaleType = ImageView.ScaleType.CENTER_INSIDE
            else
                ImageAlbum.scaleType = ImageView.ScaleType.CENTER_CROP

            GlideApp.with(root.context.applicationContext)
                .load(item.firstImage?.path)
                .error(ContextCompat.getDrawable(root.context.applicationContext, R.drawable.box_with_tint))
                .transform(transform)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(ImageAlbum)
                .clearOnDetach()

            deleteCardAlbum.setOnClickListener {
                onDeleteListener?.onClick(item)
            }

            applyCardAlbum.setOnClickListener {
                onApplyListener?.onClick(item)
            }

            root.setOnClickListener {
                callOnClick(item)
            }
        }
    }

    override fun edit(item: ImageAlbum) {
        val index = items.indexOfFirst { album ->
            album.album.id == item.album.id
        }
        if (index >= 0) {
            items[index] = item
            notifyItemChanged(index)
        }
    }

    fun setOnEditListener(callback: OnClickListener<ImageAlbum>) {
        onEditListener = callback
    }

    fun setOnApplyListener(callback: OnClickListener<ImageAlbum>) {
        onApplyListener = callback
    }

    fun setOnDeleteListener(callback: OnClickListener<ImageAlbum>) {
        onDeleteListener = callback
    }

}