package br.com.yuki.wallpaper.manager.util.custom.view.button

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isGone
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.databinding.SettingButtonBinding
import br.com.yuki.wallpaper.manager.util.global.utility.inflater
import com.google.android.material.color.MaterialColors

class SettingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(
    context, attrs, defStyleAttr, defStyleRes
) {

    fun interface OnCheckChangeListener {
        fun onCheck(isChecked: Boolean)
    }

    private val binding: SettingButtonBinding = SettingButtonBinding.inflate(inflater)
    private val checkChangeListeners: MutableList<OnCheckChangeListener> = mutableListOf()

    var isChecked: Boolean
        set(value) {
            binding.settingButtonSwitch.isOn = value
        }
        get() = binding.settingButtonSwitch.isOn

    var subtitle: CharSequence
        set(value) {
            binding.settingButtonSubtitle.text = value
        }
        get() = binding.settingButtonSubtitle.text

    init {
        addView(binding.root)

        val styledAttributes = context.obtainStyledAttributes(attrs, R.styleable.SettingButton)

        if (styledAttributes.hasValue(R.styleable.SettingButton_title))
            binding.settingButtonTitle.text = styledAttributes.getText(R.styleable.SettingButton_title)

        if (styledAttributes.hasValue(R.styleable.SettingButton_subtitle))
            binding.settingButtonSubtitle.text = styledAttributes.getText(R.styleable.SettingButton_subtitle)

        val isCheckable = styledAttributes.getBoolean(R.styleable.SettingButton_isCheckable, false)

        styledAttributes.recycle()

        binding.settingButtonDivider.isGone = !isCheckable
        binding.settingButtonSwitch.isGone = !isCheckable

        binding.settingButtonSwitch.colorOff = MaterialColors.getColor(this, R.attr.colorOnSurface)
        binding.settingButtonSwitch.colorOn = MaterialColors.getColor(this, R.attr.colorSecondaryContainer)
        binding.settingButtonSwitch.colorBorder = MaterialColors.getColor(this, R.attr.colorSecondaryContainer)

        if (isCheckable) {
            binding.root.setOnClickListener {
                binding.settingButtonSwitch.performClick()
            }
            binding.settingButtonSwitch.setOnToggledListener { _, isOn ->
                checkChangeListeners.forEach { callback ->
                    callback.onCheck(isOn)
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

    }

    fun addOnCheckChangeListener(callback: OnCheckChangeListener) {
        if (!checkChangeListeners.contains(callback))
            checkChangeListeners.add(callback)
    }

    fun removeOnCheckChangeListener(callback: OnCheckChangeListener) {
        checkChangeListeners.remove(callback)
    }

    private fun updateColor() {

    }

}