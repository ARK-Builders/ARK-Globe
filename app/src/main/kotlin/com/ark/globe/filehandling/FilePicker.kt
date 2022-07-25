package com.ark.globe.filehandling

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.ark.globe.BuildConfig
import com.ark.globe.R
import space.taran.arkfilepicker.ArkFilePickerConfig
import space.taran.arkfilepicker.ArkFilePickerFragment
import space.taran.arkfilepicker.ArkFilePickerMode

class FilePicker private constructor(){
    companion object{

        private const val TAG = "file_picker"
        const val SDK_R_READ_PERMISSIONS_CODE = 0
        var readPermLauncherSDK_R: ActivityResultLauncher<Uri>? = null
        var readPermLauncher: ActivityResultLauncher<String>? = null

        fun show(fragmentManager: FragmentManager) {
            ArkFilePickerFragment.newInstance(getFilePickerConfig()).show(fragmentManager, TAG)
        }

        fun show(activity: AppCompatActivity, fragmentManager: FragmentManager){
            if(isReadPermissionGranted(activity)){
                show(fragmentManager)
            }
            else askForReadPermissions(activity)
        }

        fun isReadPermissionGranted(activity: AppCompatActivity): Boolean{
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                Environment.isExternalStorageManager()
            else{
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
            }
        }

        private fun askForReadPermissions(activity: AppCompatActivity){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                val packageUri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, packageUri)
                activity.startActivityForResult(intent, SDK_R_READ_PERMISSIONS_CODE)
            }
            else{
                readPermLauncher?.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        fun permissionDeniedError(context: Context){
            Toast.makeText(context, context.getString(R.string.no_file_access), Toast.LENGTH_SHORT).show()
        }

        private fun getFilePickerConfig() = ArkFilePickerConfig(
            mode = ArkFilePickerMode.FOLDER,
            titleStringId = R.string.file_picker_title,
            pickButtonStringId = R.string.select
        )
    }
}