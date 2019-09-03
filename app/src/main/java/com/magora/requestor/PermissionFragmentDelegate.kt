package com.magora.requestor

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private const val REQUEST_CODE = 11

internal class PermissionFragmentDelegate : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main
    private val _resultOutput = Channel<List<Permission>>()
    private var activityStarted = false
    val resultOutput: ReceiveChannel<List<Permission>> get() = _resultOutput
    var targetPermissions: Array<String>? = null

    init {
        retainInstance = true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (targetPermissions != null && !activityStarted) {
            activityStarted = true
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(targetPermissions!!, REQUEST_CODE)
            } else {
                launch {
                    _resultOutput.send(targetPermissions!!.map { Permission(it, PackageManager.PERMISSION_GRANTED) })
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            val result = grantResults.mapIndexed { index, grantResult -> Permission(permissions[index], grantResult) }
            launch {
                _resultOutput.send(result)
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}