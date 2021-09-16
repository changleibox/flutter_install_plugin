package me.box.plugin

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import io.flutter.embedding.android.ExclusiveAppComponent
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityControlSurface
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.Registrar

/**
 * Created by changlei on 2021/9/16.
 *
 * 安装插件
 */
class InstallPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, ActivityControlSurface {
    private var mChannel: MethodChannel? = null
    private var mInstaller: Installer? = null
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPluginBinding) {
        mChannel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL)
        mChannel!!.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(flutterPluginBinding: FlutterPluginBinding) {
        mChannel!!.setMethodCallHandler(null)
    }

    override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
        when (methodCall.method) {
            "getPlatformVersion" -> result.success("Android " + Build.VERSION.RELEASE)
            "installApk" -> {
                val filePath = methodCall.argument<String>("filePath")
                val appId = methodCall.argument<String>("appId")
                try {
                    mInstaller!!.installApk(filePath, appId)
                    result.success("Success")
                } catch (e: Throwable) {
                    result.error(e.javaClass.simpleName, e.message, null)
                }
            }
            else -> result.notImplemented()
        }
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        mInstaller = Installer(activityPluginBinding.activity)
    }

    override fun onDetachedFromActivityForConfigChanges() {}
    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        mInstaller = Installer(activityPluginBinding.activity)
    }

    override fun onDetachedFromActivity() {}

    companion object {
        private const val CHANNEL = "install_plugin"

        /**
         * Registers a plugin implementation that uses the stable `io.flutter.plugin.common`
         * package.
         *
         *
         * Calling this automatically initializes the plugin. However plugins initialized this way
         * won't react to changes in activity or context, unlike CameraPlugin.
         */
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), CHANNEL)
            channel.setMethodCallHandler(InstallPlugin())
        }
    }

    override fun attachToActivity(p0: Activity, p1: Lifecycle) {
    }

    override fun attachToActivity(component: ExclusiveAppComponent<Activity>, lifecycle: Lifecycle) {
    }

    override fun detachFromActivityForConfigChanges() {
    }

    override fun detachFromActivity() {
    }

    override fun onRequestPermissionsResult(p0: Int, p1: Array<out String>, p2: IntArray): Boolean {
        return false
    }

    override fun onActivityResult(p0: Int, p1: Int, p2: Intent?): Boolean {
        return mInstaller?.onActivityResult(p0, p1, p2) == true
    }

    override fun onNewIntent(p0: Intent) {
    }

    override fun onUserLeaveHint() {
    }

    override fun onSaveInstanceState(p0: Bundle) {
    }

    override fun onRestoreInstanceState(p0: Bundle?) {
    }
}