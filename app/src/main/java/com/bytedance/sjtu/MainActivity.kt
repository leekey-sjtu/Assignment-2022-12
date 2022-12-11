package com.bytedance.sjtu

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private val etRoomId by lazy { findViewById<EditText>(R.id.et_room_id) }
    private val etUserId by lazy { findViewById<EditText>(R.id.et_user_id) }
    private val btnJoinRoom by lazy { findViewById<Button>(R.id.btn_join_room) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // todo 测试文本，需要删除
        etRoomId.setText(R.string.room_id)
        etUserId.setText(R.string.user_id)
        btnJoinRoom.setOnClickListener {
            val roomId = etRoomId.text.toString()
            val userId = etUserId.text.toString()
            joinRoom(roomId, userId)
        }
    }

    // 进入直播间
    private fun joinRoom(roomId: String, userId: String) {
        if (isFormatValid(roomId, userId)) {
            val intent = Intent(this, RoomActivity::class.java)
            intent.putExtra("room_id", roomId)
            intent.putExtra("user_id", userId)
            startActivity(intent)
        }
    }

    // 检查房间号和用户名格式
    private fun isFormatValid(roomId: String, userId: String) : Boolean {
        if (roomId.isEmpty()) {
            Toast.makeText(this, "房间号不能为空~", Toast.LENGTH_SHORT).show()
        } else if (userId.isEmpty()) {
            Toast.makeText(this, "用户名不能为空~", Toast.LENGTH_SHORT).show()
        } else if (!Pattern.matches("^[a-zA-Z0-9@._-]{1,128}$", roomId)) {
            Toast.makeText(this, "房间号格式错误~", Toast.LENGTH_SHORT).show()
        } else if (!Pattern.matches("^[a-zA-Z0-9@._-]{1,128}$", userId)) {
            Toast.makeText(this, "用户名格式错误~", Toast.LENGTH_SHORT).show()
        } else {
            return true
        }
        return false
    }

}