package top.intkilow.architecture.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import java.util.*


class PermissionUtil {

    companion object {
        /**
         * 去设置 通知权限
         * 1、判断应用是否有通知权限
         * NotificationManagerCompat.from(context).areNotificationsEnabled()
         */
        fun goToSettingNotification(context: Context, fragment: String? = null) {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= 26) { // android 8.0引导
                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                intent.putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
            } else { //其它
                intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                intent.data = Uri.fromParts("package", context.packageName, fragment)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        fun hasPermissions(context: Context, permission: String): Boolean {
            return hasPermissions(context, ArrayList<String>().apply {
                add(permission)
            }).size <= 0
        }
        fun hasPermissions(context: Context, permissions: ArrayList<String>): TreeSet<String> {
            val noPermissions = TreeSet<String>()
            for (p in permissions) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        p
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // 无权限
                    noPermissions.add(p)
                }
            }
            return noPermissions
        }
    }
}