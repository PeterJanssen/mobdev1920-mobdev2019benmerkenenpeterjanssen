package be.pxl.mobdev2019.cityWatch.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import be.pxl.mobdev2019.cityWatch.R

import com.skydoves.colorpickerpreference.ColorPickerPreference
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class SettingsFragment : PreferenceFragmentCompat() {

    private val customBuilder: ColorPickerDialog.Builder
        get() {
            val builder = ColorPickerDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
            builder.setTitle("ColorPicker Dialog")
            builder.colorPickerView.flagView =
                CustomFlag(activity!!.baseContext, R.layout.layout_flag)
            builder.setPositiveButton(
                getString(R.string.confirm),
                ColorEnvelopeListener { _, _ -> })
            builder.setNegativeButton(
                getString(R.string.cancel)
            ) { dialogInterface, _ -> dialogInterface.dismiss() }

            builder.colorPickerView.flagView.flagMode = FlagMode.LAST
            return builder
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_settings)
        val button: Preference = findPreference(getString(R.string.clearPreferences))!!
        button.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit().clear().apply()
            PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putString(
                    activity!!.getString(R.string.clearPreferences),
                    activity!!.getString(R.string.clearPreferences)
                ).apply()
            true
        }
        initColorPickerPreference()
    }

    private fun initColorPickerPreference() {
        val colorPickerPreferenceToolbar =
            findPreference<ColorPickerPreference>(activity!!.getString(R.string.toolbarColorPickerPreference))
        colorPickerPreferenceToolbar?.colorPickerDialogBuilder = customBuilder

        val colorPickerPreferenceNavBarBackground =
            findPreference<ColorPickerPreference>(activity!!.getString(R.string.navBarBackgroundColorPickerPreference))
        colorPickerPreferenceNavBarBackground?.colorPickerDialogBuilder = customBuilder
    }
}
