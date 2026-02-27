package com.litelauncher.model

data class AppInfo(
    val label: String,
    val packageName: String,
    val activityName: String,
    val isSystem: Boolean = false
)
