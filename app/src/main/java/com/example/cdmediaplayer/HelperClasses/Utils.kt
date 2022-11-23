package com.example.cdmediaplayer.HelperClasses

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import java.util.jar.Manifest
//By Diego Cobos
//Helper Class that handles permission cases
class Utils {
    infix fun isPermissionGranted(context: Context): Boolean {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager()
        }
        else{
            var readExtStorage = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
            return readExtStorage == PackageManager.PERMISSION_GRANTED
        }
    }

}