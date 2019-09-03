package com.magora.requestor

import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

data class Permission(val name: String, val grantedCode: Int) {
    val isGranted = grantedCode == PackageManager.PERMISSION_GRANTED
    val isDenied = grantedCode == PackageManager.PERMISSION_DENIED
}

private val SHOULD_REQUEST_PERMISSIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

class Requestor : CoroutineScope {
    private var caller: Any? = null
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main

    fun on(activity: FragmentActivity) = apply { caller = activity }

    fun on(fragment: Fragment) = apply { caller = fragment }

    fun request(permissions: Array<String>, onResult: (List<Permission>) -> Unit): Job? =
        if (SHOULD_REQUEST_PERMISSIONS) {
            when (val anyCaller = caller ?: throw NullPointerException("Caller isn't set")) {
                is FragmentActivity -> launch { onResult(startForActivity(anyCaller, permissions)) }
                is Fragment -> launch { onResult(startForFragment(anyCaller, permissions)) }
                else -> null    //Should never happens
            }
        } else {
            onResult(permissions.map { Permission(it, PackageManager.PERMISSION_GRANTED) })
            null
        }

    suspend fun request(permissions: Array<String>): List<Permission> =
        if (SHOULD_REQUEST_PERMISSIONS) {
            when (val anyCaller = caller ?: throw NullPointerException("Caller isn't set")) {
                is FragmentActivity -> startForActivity(anyCaller, permissions)
                is Fragment -> startForFragment(anyCaller, permissions)
                else -> throw RuntimeException("Unknown caller")
            }
        } else {
            permissions.map { Permission(it, PackageManager.PERMISSION_GRANTED) }
        }

    private suspend fun startForActivity(activity: FragmentActivity, permissions: Array<String>): List<Permission> =
        requestPermissionsInternal(activity.supportFragmentManager, permissions)

    private suspend fun startForFragment(fragment: Fragment, permissions: Array<String>): List<Permission> =
        requestPermissionsInternal(fragment.childFragmentManager, permissions)

    private suspend fun requestPermissionsInternal(fm: FragmentManager, permissions: Array<String>): List<Permission> {
        try {
            if (fm.isStateSaved) return emptyList()

            val fd = PermissionFragmentDelegate()
            fd.targetPermissions = permissions
            fm.beginTransaction().add(fd, null).commitNow()

            if (fm.isStateSaved) return emptyList()

            val result = fd.resultOutput.receive()

            if (fm.isStateSaved) return emptyList()

            fm.beginTransaction().remove(fd).commitNow()
            return result
        } catch (ignored: Exception) {
            ignored.printStackTrace()
            return emptyList()
        }
    }
}