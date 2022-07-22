package com.ark.globe.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

class FileAccessContract: ActivityResultContract<Uri, Unit>() {

    override fun createIntent(context: Context, input: Uri): Intent {
        return Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?) = Unit
}