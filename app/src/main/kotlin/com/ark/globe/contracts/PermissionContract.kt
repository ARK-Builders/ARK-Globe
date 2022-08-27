package com.ark.globe.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

class PermissionContract: ActivityResultContract<String, Unit>() {
    override fun createIntent(context: Context, input: String): Intent {
       return Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse(input))
    }

    override fun parseResult(resultCode: Int, intent: Intent?) = Unit
}