package br.com.yuki.wallpaper.manager.screen.other.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import br.com.yuki.wallpaper.manager.databinding.CardQuestionsBinding
import br.com.yuki.wallpaper.manager.model.Questions
import br.com.yuki.wallpaper.manager.util.base.adapter.BaseAdapter
import br.com.yuki.wallpaper.manager.util.global.utility.inflater

class AdapterQuestions : BaseAdapter<Questions, AdapterQuestions.Holder>() {

    private val opened: MutableList<Int> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        CardQuestionsBinding.inflate(parent.inflater, parent, false).root
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = get(position)

        configure(holder, position)
        holder.binding.titleQuestions.text = item.title
        holder.binding.subtitleQuestions.text = item.subtitle

        holder.binding.root.setOnClickListener {
            if (opened.contains(position))
                opened.remove(position)
            else
                opened.add(position)
            configure(holder, position)
        }

    }

    private fun configure(holder: Holder, position: Int) {
        if (opened.contains(position)) {
            holder.binding.dividerQuestions.isGone = false
            holder.binding.subtitleQuestions.isGone = false
        } else {
            holder.binding.dividerQuestions.isGone = true
            holder.binding.subtitleQuestions.isGone = true

        }
    }

    class Holder(view: View) : BaseAdapter.Holder(view) {

        val binding: CardQuestionsBinding = CardQuestionsBinding.bind(view)

    }

}