package br.com.yuki.wallpaper.manager.screen.home.fragment.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import br.com.yuki.wallpaper.manager.R
import br.com.yuki.wallpaper.manager.databinding.FragmentAlarmDetailsBinding
import br.com.yuki.wallpaper.manager.model.Alarm
import br.com.yuki.wallpaper.manager.screen.home.fragment.alarm.dialog.AlarmAlbumBottomFragment
import br.com.yuki.wallpaper.manager.screen.home.fragment.settings.AdapterAlarm
import br.com.yuki.wallpaper.manager.util.global.utility.statusBarHeight
import br.com.yuki.wallpaper.manager.util.base.factory.alarmViewModels
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class AlarmFragment : Fragment() {

    private var binding: FragmentAlarmDetailsBinding? = null

    private val viewModel: AlarmViewModel by alarmViewModels()

    private val adapter: AdapterAlarm = AdapterAlarm().apply {
        setOnUpdateListener { alarm, state ->
            when (state) {
                AdapterAlarm.OnUpdateListener.State.PICKER -> MaterialTimePicker.Builder()
                    .setHour(alarm.hour)
                    .setMinute(alarm.minute)
                    .setTitleText(R.string.time)
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setNegativeButtonText(R.string.cancel)
                    .setPositiveButtonText(R.string.ok)
                    .build()
                    .also { picker ->
                        picker.addOnPositiveButtonClickListener {
                            viewModel.update(alarm.apply {
                                hour = picker.hour
                                minute = picker.minute
                            }).observe(viewLifecycleOwner) { isEdited ->
                                if (isEdited)
                                    this.edit(alarm)
                            }
                        }
                    }
                    .show(childFragmentManager, null)
                AdapterAlarm.OnUpdateListener.State.DELETE -> viewModel.cancel(alarm).observe(viewLifecycleOwner) { canDelete ->
                    if (canDelete)
                        this.remove(alarm)
                }
                AdapterAlarm.OnUpdateListener.State.UPDATE -> viewModel.update(alarm).observe(viewLifecycleOwner) { canDelete ->
                    if (canDelete)
                        this.edit(alarm)
                }
                AdapterAlarm.OnUpdateListener.State.ALBUM -> AlarmAlbumBottomFragment()
                    .also { sheet ->
                        sheet.setOnSelectListener { newAlbum ->
                            sheet.dismiss()
                            viewModel.update(alarm.apply {
                                albumId = newAlbum.id
                                albumName = newAlbum.name
                            })
                            this.edit(alarm)
                        }
                    }
                    .show(childFragmentManager, null)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlarmDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.root?.updatePadding(top = statusBarHeight)
        bindingView()
    }

    private fun bindingView() {
        viewModel.getAll().observe(viewLifecycleOwner, adapter::set)
        binding?.listAlarm?.adapter = adapter
        binding?.addAlarm?.setOnClickListener {
            val alarm = Alarm.default
            viewModel.add(alarm).observe(viewLifecycleOwner) { id ->
                if (id != null) {
                    alarm.id = id
                    adapter.add(alarm)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}