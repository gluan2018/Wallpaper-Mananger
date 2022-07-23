package br.com.yuki.wallpaper.manager.screen.other

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.databinding.ActivityHelpBinding
import br.com.yuki.wallpaper.manager.model.Questions
import br.com.yuki.wallpaper.manager.screen.other.adapter.AdapterQuestions
import com.google.android.material.color.MaterialColors
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HelpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityHelpBinding.inflate(layoutInflater).apply {
            setContentView(root)
            toolbarHelp.setNavigationOnClickListener { finish() }

            questionsHelp.adapter = AdapterQuestions().also { adapterQuestions ->
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            val localeIsPtBr = resources.configuration.locales.get(0).language.equals(Locale("pt-br").language)
                            Gson().fromJson(
                                resources.openRawResource(
                                    if (localeIsPtBr)
                                        R.raw.questions_br
                                    else
                                        R.raw.questions
                                ).reader(),
                                Array<Questions>::class.java
                            )
                        }.onSuccess { questions ->
                            withContext(Dispatchers.Main) {
                                adapterQuestions.set(questions.toList())
                            }
                        }
                    }
                }
            }
        }
    }

}