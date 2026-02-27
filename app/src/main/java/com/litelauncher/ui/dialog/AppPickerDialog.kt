package com.litelauncher.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.litelauncher.databinding.DialogAppPickerBinding
import com.litelauncher.model.AppInfo
import com.litelauncher.search.FuzzySearch
import com.litelauncher.ui.adapter.AppListAdapter

class AppPickerDialog : DialogFragment() {

    private var allApps: List<AppInfo> = emptyList()
    private var onAppPicked: ((AppInfo) -> Unit)? = null

    companion object {
        fun newInstance(apps: List<AppInfo>, onPicked: (AppInfo) -> Unit): AppPickerDialog {
            return AppPickerDialog().apply {
                allApps = apps
                onAppPicked = onPicked
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAppPickerBinding.inflate(layoutInflater)

        val adapter = AppListAdapter(
            onClick = { app ->
                onAppPicked?.invoke(app)
                dismiss()
            },
            onLongClick = {}
        )

        binding.pickerRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.pickerRecycler.adapter = adapter
        adapter.submitList(allApps)

        binding.pickerSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty()
                val filtered = if (query.isBlank()) allApps else FuzzySearch.filter(allApps, query)
                adapter.submitList(filtered)
            }
        })

        return Dialog(requireContext()).apply {
            setContentView(binding.root)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}
