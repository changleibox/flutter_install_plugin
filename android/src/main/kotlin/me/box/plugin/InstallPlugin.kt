package me.box.plugin

import android.os.Build
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
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
class InstallPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
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

    private var mChannel: MethodChannel? = null
    private var mInstaller: Installer? = null
    private var mActivityPluginBinding: ActivityPluginBinding? = null

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
        attachedToActivity(activityPluginBinding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        detachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        attachedToActivity(activityPluginBinding)
    }

    override fun onDetachedFromActivity() {
        detachedFromActivity()
    }

    private fun attachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        if (mInstaller != null) {
            activityPluginBinding.removeActivityResultListener(mInstaller!!)
        }
        mActivityPluginBinding = activityPluginBinding
        mInstaller = Installer(activityPluginBinding.activity)
        activityPluginBinding.addActivityResultListener(mInstaller!!)
    }

    private fun detachedFromActivity() {
        if (mActivityPluginBinding != null && mInstaller != null) {
            mActivityPluginBinding!!.removeActivityResultListener(mInstaller!!)
        }
        mActivityPluginBinding = null;
        mInstaller = null;
    }
}