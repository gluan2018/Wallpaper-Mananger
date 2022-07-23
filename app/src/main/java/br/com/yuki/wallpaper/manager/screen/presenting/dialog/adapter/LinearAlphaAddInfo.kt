package br.com.yuki.wallpaper.manager.screen.presenting.dialog.adapter

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class LinearAlphaAddInfo(context: Context) : LinearLayoutManager(context, VERTICAL, false) {

    init {
        stackFromEnd = true
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        changeAlpha()
    }

    private fun changeAlpha() {
        for (index in 0 until childCount) {
            val child = getChildAt(index) ?: continue
            val alpha = max(height - child.bottom, 0)
            child.alpha = 1f - alpha.div(height.toFloat())
        }
    }

}