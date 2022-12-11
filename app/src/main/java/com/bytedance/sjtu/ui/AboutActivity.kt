package com.bytedance.sjtu.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.bytedance.sjtu.R
import com.ss.bytertc.engine.RTCEngine

class AboutActivity : AppCompatActivity() {
    private val tvSdkVersion by lazy { findViewById<TextView>(R.id.tv_sdk_version_1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val sdkVersion = RTCEngine.getSdkVersion()
        tvSdkVersion.text = sdkVersion
    }
}