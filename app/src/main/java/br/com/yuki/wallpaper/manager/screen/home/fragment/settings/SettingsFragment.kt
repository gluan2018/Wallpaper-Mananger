package br.com.yuki.wallpaper.manager.screen.home.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.databinding.FragmentSettingsBinding
import br.com.yuki.wallpaper.manager.model.Configuration
import br.com.yuki.wallpaper.manager.util.global.utility.copy
import br.com.yuki.wallpaper.manager.util.global.utility.statusBarHeight
import br.com.yuki.wallpaper.manager.util.tools.Preferences
import br.com.yuki.wallpaper.manager.util.tools.getOrDefault
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SettingsFragment : Fragment() {

    private var binding: FragmentSettingsBinding? = null

    private val requestImage = registerForActivityResult(ActivityResultContracts.GetContent()) { image ->
        if (image != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                runCatching {
                    lockscreenFile().apply {
                        if (!exists())
                            createNewFile()
                        image.copy(requireContext(), to = this)
                    }
                }.onFailure {
                    it.printStackTrace()
                    withContext(Dispatchers.Main) {
                        binding?.lockscreenNone?.isChecked = true
                    }
                }
            }
        } else
            binding?.lockscreenNone?.isChecked = true
    }

    private val preferences: Preferences<Configuration> by lazy {
        Preferences.config(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.root?.updatePadding(top = statusBarHeight)

        loadValues()
        bindingView()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun loadValues() {
        preferences.getOrDefault().apply {
            binding?.shuffle?.isChecked = this.shuffleAlbum
            binding?.darkenWallpaper?.subtitle = "${(this.alpha / 255f * 100f).toInt()}%"
            binding?.darkenSeek?.progress = this.alpha
            binding?.darkenImage?.alpha = this.alpha / 255f
            binding?.doubleTapping?.isChecked = this.doubleClick

            binding?.time?.subtitle = (if (this.timer.hours > 0L) "${this.timer.hours} h " else "") + "${this.timer.minutes} min"

            binding?.changeWhenLock?.isChecked = this.changeWhenLock

            lockscreenFile().apply {
                binding?.lockscreenImage?.isChecked = this.exists()
                binding?.lockscreenNone?.isChecked = !this.exists()
            }
        }
    }

    private fun bindingView() {
        val item = preferences.getOrDefault()

        binding?.shuffle?.addOnCheckChangeListener { isChecked ->
            item.shuffleAlbum = isChecked
            update(configuration = item)
        }
        binding?.darkenSeek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding?.darkenImage?.alpha = progress / 255f
                binding?.darkenWallpaper?.subtitle = "${(progress / 255f * 100).toInt()}%"
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                item.alpha = seekBar?.progress ?: item.alpha
                update(configuration = item)
            }
        })
        binding?.darkenWallpaper?.setOnClickListener {
            binding?.layoutDarken?.apply {
                isGone = !isGone
            }
        }
        binding?.doubleTapping?.addOnCheckChangeListener { isChecked ->
            item.doubleClick = isChecked
            update(configuration = item)
        }
        binding?.time?.setOnClickListener {
            MaterialTimePicker.Builder()
                .setHour(item.timer.hours.toInt())
                .setMinute(item.timer.minutes.toInt())
                .setTitleText(R.string.time)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setNegativeButtonText(R.string.cancel)
                .setPositiveButtonText(R.string.ok)
                .build()
                .also { picker ->
                    picker.addOnPositiveButtonClickListener {
                        binding?.time?.subtitle = (if (picker.hour > 0L) "${picker.hour} h " else "") + "${picker.minute} min"
                        item.timer.hours = picker.hour.toLong()
                        item.timer.minutes = picker.minute.toLong()
                        update(configuration = item)
                    }
                }
                .show(childFragmentManager, null)
        }
        binding?.changeWhenLock?.addOnCheckChangeListener { isChecked ->
            item.changeWhenLock = isChecked
            update(configuration = item)
        }
        binding?.lockscreen?.setOnClickListener {
            binding?.lockscreenGroup?.apply {
                isGone = !isGone
            }
            binding?.lockscreenGroup?.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    binding?.lockscreenImage?.id -> requestImage.launch("image/*")
                    binding?.lockscreenNone?.id -> runCatching { lockscreenFile().delete() }
                }
            }
        }

    }

    private fun update(configuration: Configuration) {
        preferences.current = configuration
    }

    private fun lockscreenFile(): File = File(requireContext().dataDir, "lockscreen")

}