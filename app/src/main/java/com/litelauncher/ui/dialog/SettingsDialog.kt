package com.litelauncher.ui.dialog

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.litelauncher.R
import com.litelauncher.databinding.DialogSettingsBinding
import com.litelauncher.util.BackgroundImages
import com.litelauncher.viewmodel.SettingsViewModel

class SettingsDialog : DialogFragment() {

    private var settingsViewModel: SettingsViewModel? = null
    private var onSettingsChanged: (() -> Unit)? = null

    companion object {
        fun newInstance(
            viewModel: SettingsViewModel,
            onChanged: () -> Unit
        ): SettingsDialog {
            return SettingsDialog().apply {
                settingsViewModel = viewModel
                onSettingsChanged = onChanged
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogSettingsBinding.inflate(layoutInflater)
        val vm = settingsViewModel ?: return Dialog(requireContext())

        // Accent colors
        val accentColors = resources.getIntArray(R.array.accent_colors) ?: intArrayOf()
        binding.accentColorRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.accentColorRecycler.adapter = ColorSwatchAdapter(accentColors.toList()) { index ->
            vm.setAccentColor(index)
            onSettingsChanged?.invoke()
        }

        // Background selector
        binding.backgroundRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.backgroundRecycler.adapter = BackgroundOptionAdapter { index ->
            vm.setBackground(index)
            onSettingsChanged?.invoke()
        }

        // Show system apps
        binding.showSystemAppsSwitch.isChecked = vm.showSystemApps.value ?: false
        binding.showSystemAppsSwitch.setOnCheckedChangeListener { _, checked ->
            vm.setShowSystemApps(checked)
            onSettingsChanged?.invoke()
        }

        // Set default launcher
        binding.setDefaultLauncherButton.setOnClickListener {
            startActivity(Intent(android.provider.Settings.ACTION_HOME_SETTINGS))
        }

        return Dialog(requireContext()).apply {
            setContentView(binding.root)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    // Simple color swatch adapter
    private class ColorSwatchAdapter(
        private val colors: List<Int>,
        private val onPick: (Int) -> Unit
    ) : RecyclerView.Adapter<ColorSwatchAdapter.VH>() {

        class VH(val view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val size = (44 * parent.resources.displayMetrics.density).toInt()
            val margin = (4 * parent.resources.displayMetrics.density).toInt()
            val view = View(parent.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(size, size).apply {
                    setMargins(margin, margin, margin, margin)
                }
                focusable = View.FOCUSABLE
            }
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val color = colors[position]
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
                setStroke(2, Color.WHITE)
            }
            holder.view.background = bg
            holder.view.setOnClickListener { onPick(position) }
        }

        override fun getItemCount() = colors.size
    }

    // Background option adapter (-1=none, 0..9=images)
    private class BackgroundOptionAdapter(
        private val onPick: (Int) -> Unit
    ) : RecyclerView.Adapter<BackgroundOptionAdapter.VH>() {

        class VH(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val dp = parent.resources.displayMetrics.density
            val tv = TextView(parent.context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    (80 * dp).toInt(), (44 * dp).toInt()
                ).apply {
                    val m = (4 * dp).toInt()
                    setMargins(m, m, m, m)
                }
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                textSize = 14f
                focusable = View.FOCUSABLE
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 4 * dp
                    setColor(0xFF333333.toInt())
                    setStroke(1, Color.GRAY)
                }
            }
            return VH(tv)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val index = position - 1 // -1 = none
            holder.textView.text = if (index < 0) "None" else "${index + 1}"
            holder.textView.setOnClickListener { onPick(index) }
        }

        override fun getItemCount() = BackgroundImages.IMAGE_COUNT + 1
    }
}
