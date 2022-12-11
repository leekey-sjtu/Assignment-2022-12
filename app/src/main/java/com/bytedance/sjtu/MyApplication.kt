package com.bytedance.sjtu

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        const val APP_ID = "634c1df0c8217a0146f8b05f"

        /**
         * 用户ID: wdw
         * 房间ID: wdwroom
         * TOKEN过期时间: 2022/10/23
         */
        const val TOKEN = "001634c1df0c8217a0146f8b05fQAB/oZ8BgntTYwK2XGMHAHdkd3Jvb20DAHdkdwYAAAACtlxjAQACtlxjAgACtlxjAwACtlxjBAACtlxjBQACtlxjIACucYniNUgH+f5cYYFOC6U0AcAetdpuNCaMtCOvxfoWRA==";

        /**
         * 用户ID: wdw2
         * 房间ID: wdwroom
         * TOKEN过期时间: 2022/10/24
         */
//        const val TOKEN = "001634c1df0c8217a0146f8b05fQQANWmkCpHtTYyS2XGMHAHdkd3Jvb20EAHdkdzIGAAAAJLZcYwEAJLZcYwIAJLZcYwMAJLZcYwQAJLZcYwUAJLZcYyAAfnojerh52+Kx9qI9ruNUZgodOq6RjLL7QOsO3tj1Qzo="

        /**
         * 用户ID: wdw3
         * 房间ID: wdwroom
         * TOKEN过期时间: 2022/10/29
         */
//        const val TOKEN = "001634c1df0c8217a0146f8b05fQQCLbMoDO3tTY7u1XGMHAHdkd3Jvb20EAHdkdzMGAAAAu7VcYwEAu7VcYwIAu7VcYwMAu7VcYwQAu7VcYwUAu7VcYyAA0d52oVGQpCwFK5OZdBMhm4OOuEJzjwSbmtaGyXwHwhQ="

        /**
         * 测试直播视频地址
         */
        const val liveUrl = "https://vdse.bdstatic.com/54cc13024b92d09fc2c67c5cee8afc9f.mp4"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}