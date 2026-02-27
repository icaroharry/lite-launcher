package com.litelauncher.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.litelauncher.LiteLauncherApp
import com.litelauncher.databinding.ActivityMainBinding
import com.litelauncher.model.AppInfo
import com.litelauncher.model.FavoriteSlot
import com.litelauncher.ui.adapter.AppListAdapter
import com.litelauncher.ui.adapter.FavoritesAdapter
import com.litelauncher.ui.dialog.AppPickerDialog
import com.litelauncher.ui.dialog.SettingsDialog
import com.litelauncher.util.BackgroundImages
import com.litelauncher.viewmodel.LauncherViewModel
import com.litelauncher.viewmodel.LauncherViewModelFactory
import com.litelauncher.viewmodel.SettingsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var launcherVm: LauncherViewModel
    private lateinit var settingsVm: SettingsViewModel
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var appListAdapter: AppListAdapter
    private var clockTimer: Timer? = null

    private val packageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            launcherVm.loadApps()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as LiteLauncherApp
        val factory = LauncherViewModelFactory(app.appRepository, app.preferencesManager)
        launcherVm = ViewModelProvider(this, factory)[LauncherViewModel::class.java]
        settingsVm = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        setupFavorites()
        setupAppList()
        setupSearch()
        setupSettings()
        observeViewModels()
        registerPackageReceiver()

        launcherVm.loadApps()
    }

    private fun setupFavorites() {
        favoritesAdapter = FavoritesAdapter(
            onClick = { slot -> onFavoriteClick(slot) },
            onLongClick = { slot -> onFavoriteLongClick(slot) }
        )
        binding.favoritesRecycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.favoritesRecycler.adapter = favoritesAdapter
    }

    private fun setupAppList() {
        appListAdapter = AppListAdapter(
            onClick = { app -> launchApp(app) },
            onLongClick = { app -> showAppOptions(app) }
        )
        binding.appsRecycler.layoutManager = LinearLayoutManager(this)
        binding.appsRecycler.adapter = appListAdapter
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                launcherVm.search(s?.toString().orEmpty())
            }
        })

        binding.searchInput.setOnEditorActionListener { _, _, _ ->
            val apps = launcherVm.filteredApps.value
            if (!apps.isNullOrEmpty()) {
                launchApp(apps.first())
                binding.searchInput.text?.clear()
            }
            hideKeyboard()
            true
        }
    }

    private fun setupSettings() {
        binding.settingsButton.setOnClickListener {
            SettingsDialog.newInstance(settingsVm) {
                applyTheme()
                launcherVm.loadApps()
            }.show(supportFragmentManager, "settings")
        }
    }

    private fun observeViewModels() {
        launcherVm.favorites.observe(this) { favorites ->
            favoritesAdapter.submitList(favorites)
            val hasAny = favorites.any { it.appInfo != null }
            binding.favoritesLabel.visibility =
                if (hasAny) android.view.View.VISIBLE else android.view.View.GONE
            binding.favoritesRecycler.visibility =
                if (hasAny) android.view.View.VISIBLE else android.view.View.GONE
        }
        launcherVm.filteredApps.observe(this) { apps ->
            appListAdapter.submitList(apps) {
                // After list is committed, give focus to the first item if nothing else has focus
                if (!binding.searchInput.hasFocus()) {
                    binding.appsRecycler.post {
                        val first = binding.appsRecycler.findViewHolderForAdapterPosition(0)
                        first?.itemView?.requestFocus()
                    }
                }
            }
        }
        settingsVm.backgroundIndex.observe(this) { applyBackground(it) }
    }

    private fun onFavoriteClick(slot: FavoriteSlot) {
        if (slot.appInfo != null) {
            launchApp(slot.appInfo)
        } else {
            openAppPicker(slot.index)
        }
    }

    private fun onFavoriteLongClick(slot: FavoriteSlot) {
        if (slot.appInfo != null) {
            launcherVm.removeFavorite(slot.index)
        } else {
            openAppPicker(slot.index)
        }
    }

    private fun openAppPicker(slotIndex: Int) {
        val apps = launcherVm.allApps.value.orEmpty()
        AppPickerDialog.newInstance(apps) { app ->
            launcherVm.setFavorite(slotIndex, app)
        }.show(supportFragmentManager, "picker")
    }

    private fun showAppOptions(app: AppInfo) {
        // Find next empty favorite slot and assign
        val slots = launcherVm.favorites.value.orEmpty()
        val emptySlot = slots.firstOrNull { it.appInfo == null }
        if (emptySlot != null) {
            launcherVm.setFavorite(emptySlot.index, app)
            Toast.makeText(this, "${app.label} added to favorites", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "All favorite slots are full", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchApp(app: AppInfo) {
        val intent = packageManager.getLeanbackLaunchIntentForPackage(app.packageName)
            ?: packageManager.getLaunchIntentForPackage(app.packageName)
        if (intent != null) {
            startActivity(intent)
            binding.searchInput.text?.clear()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    private fun applyTheme() {
        applyBackground(settingsVm.backgroundIndex.value ?: -1)
    }

    private fun applyBackground(index: Int) {
        val resId = BackgroundImages.getResourceId(index)
        if (resId != 0) {
            binding.backgroundImage.setImageResource(resId)
            binding.backgroundImage.visibility = android.view.View.VISIBLE
            binding.overlay.visibility = android.view.View.VISIBLE
        } else {
            binding.backgroundImage.visibility = android.view.View.GONE
            binding.overlay.visibility = android.view.View.GONE
        }
    }

    private fun startClock() {
        clockTimer = Timer().apply {
            scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    val now = SimpleDateFormat("EEE, MMM d  h:mm a", Locale.getDefault()).format(Date())
                    runOnUiThread { binding.clockText.text = now }
                }
            }, 0, 30_000)
        }
    }

    private fun registerPackageReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(packageReceiver, filter)
    }

    override fun onResume() {
        super.onResume()
        startClock()
        launcherVm.loadApps()
        // Ensure keyboard is hidden and search doesn't steal focus on resume
        binding.searchInput.clearFocus()
        hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        clockTimer?.cancel()
        clockTimer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(packageReceiver)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Home pressed while already on launcher — scroll to top, clear search
        binding.searchInput.text?.clear()
        binding.favoritesRecycler.scrollToPosition(0)
        binding.appsRecycler.scrollToPosition(0)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // If user starts typing, focus the search field
        if (keyCode in KeyEvent.KEYCODE_A..KeyEvent.KEYCODE_Z && !binding.searchInput.hasFocus()) {
            binding.searchInput.requestFocus()
            return super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }
}
