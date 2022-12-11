package com.bytedance.sjtu.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bytedance.sjtu.MyApplication.Companion.context

object PermissionUtils {

    // 是否获得全部权限
    fun allPermissionsGranted(permissionsRequired: MutableList<String>): Boolean {
        Log.d("wdw", "allPermissionsGranted")
        return permissionsRequired.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 申请未获得的权限
    fun requestPermissions(activity: Activity, permissionsRequired: MutableList<String>, permissionRequestCode: Int) {
        Log.d("wdw", "requestPermission")
        val permissions = mutableListOf<String>()
        for (per in permissionsRequired) {
            if (ContextCompat.checkSelfPermission(context, per) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per)
            }
        }
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), permissionRequestCode)
    }

}