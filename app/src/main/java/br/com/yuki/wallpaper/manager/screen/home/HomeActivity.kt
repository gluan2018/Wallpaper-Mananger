package br.com.yuki.wallpaper.manager.screen.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.databinding.ActivityHomeBinding
import br.com.yuki.wallpaper.manager.screen.home.fragment.album.AlbumFragment
import br.com.yuki.wallpaper.manager.screen.home.fragment.home.HomeFragment
import br.com.yuki.wallpaper.manager.screen.home.fragment.settings.SettingsFragment
import com.google.android.material.color.MaterialColors

class HomeActivity : AppCompatActivity() {

    companion object {
        private const val KEY = "save_current_id"
    }

    private val fragments: Map<Int, Class<out Fragment>> = mapOf(
        Pair(R.id.HomeMenu, HomeFragment::class.java),
        Pair(R.id.AlbumMenu, AlbumFragment::class.java),
        Pair(R.id.SettingsMenu, SettingsFragment::class.java)
    )

    var topInset: Int = 0
        private set

    private var currentId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { _, inset ->
            val systemBar = inset.getInsets(WindowInsetsCompat.Type.navigationBars())
            topInset = systemBar.top
            binding.BottomNavigation.updatePadding(bottom = systemBar.bottom)
            return@setOnApplyWindowInsetsListener WindowInsetsCompat.CONSUMED
        }

        binding.BottomNavigation.setOnItemReselectedListener { }
        binding.BottomNavigation.setOnItemSelectedListener {
            replace(binding, it.itemId)
            return@setOnItemSelectedListener true
        }

        val id = savedInstanceState?.getInt(KEY, -1)?.takeIf { valueId -> valueId != -1 }
        replace(binding, id ?: R.id.HomeMenu)
    }

    private fun replace(binding: ActivityHomeBinding, id: Int) {
        if (id == R.id.HomeMenu)
            WindowCompat.getInsetsController(window, window.decorView.rootView).isAppearanceLightStatusBars = false
        else {
            WindowCompat.getInsetsController(window, window.decorView.rootView).isAppearanceLightStatusBars = MaterialColors.isColorLight(
                MaterialColors.getColor(this, R.attr.background, ContextCompat.getColor(this, R.color.md_theme_background))
            )
        }

        currentId = id
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .replace(binding.FrameFragment.id, fragments.getValue(id).newInstance())
            .commit()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentId?.apply {
            outState.putInt(KEY, this)
        }
    }

}
