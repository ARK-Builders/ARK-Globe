package com.ark.globe.contracts

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.EnvironmentCompat

class PermissionContract: ActivityResultContract<String, Boolean>() {
    override fun createIntent(context: Context, input: String): Intent {
       return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse(input))
    }

    override fun parseResult(resultCode: Int, intent: Intent?) = Environment.isExternalStorageManager()
}