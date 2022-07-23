package br.com.yuki.wallpaper.manager.screen.presenting.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.DialogFragment
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.database.model.image.Image
import br.com.yuki.wallpaper.manager.databinding.FragmentLoadingImageBinding
import br.com.yuki.wallpaper.manager.screen.presenting.dialog.adapter.InfoAddAdapter
import br.com.yuki.wallpaper.manager.screen.presenting.dialog.adapter.LinearAlphaAddInfo

class LoadingImagesDialog : DialogFragment() {

    private var binding: FragmentLoadingImageBinding? = null
    private var current: Int = 0
    private var max: Int = 0

    private var adapter: InfoAddAdapter = InfoAddAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoadingImageBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.infoProgress?.text = context?.getString(R.string.progress_value, current, max)
        binding?.progressLoading?.max = max
        binding?.progressLoading?.progress = current
        binding?.infoAdd?.adapter = adapter
        binding?.infoAdd?.layoutManager = LinearAlphaAddInfo(requireContext())

        binding?.infoAdd?.setOnTouchListener { _, _ -> false }

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, _ ->
            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_WallpaperManager_DialogBase)
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            ViewCompat.setOnApplyWindowInsetsListener(this.decorView.rootView) { _, inset ->
                val systemBar = inset.getInsets(WindowInsetsCompat.Type.systemBars())
                binding?.root?.updatePadding(bottom = systemBar.bottom)
                return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : ComponentDialog(requireContext(), theme) {
            override fun onBackPressed() = Unit
        }
    }

    fun setProgress(current: Int, max: Int, newImage: Image?) {
        this.current = current
        this.max = max

        if (newImage != null)
            adapter.add(newImage)

        binding?.infoProgress?.text = context?.getString(R.string.progress_value, current, max)
        binding?.progressLoading?.max = max
        binding?.progressLoading?.setProgress(current, true)
    }

}