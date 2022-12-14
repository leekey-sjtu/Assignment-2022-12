package com.bytedance.sjtu

import android.Manifest
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.TextureView
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.sjtu.MyApplication.Companion.APP_ID
import com.bytedance.sjtu.MyApplication.Companion.TOKEN
import com.bytedance.sjtu.MyApplication.Companion.context
import com.bytedance.sjtu.utils.PermissionUtils.allPermissionsGranted
import com.bytedance.sjtu.utils.PermissionUtils.requestPermissions
import com.ss.bytertc.engine.*
import com.ss.bytertc.engine.data.AVSyncState
import com.ss.bytertc.engine.data.RemoteStreamKey
import com.ss.bytertc.engine.data.StreamIndex
import com.ss.bytertc.engine.data.VideoFrameInfo
import com.ss.bytertc.engine.handler.IRTCRoomEventHandler
import com.ss.bytertc.engine.handler.IRTCVideoEventHandler
import com.ss.bytertc.engine.type.*
import java.nio.ByteBuffer

class RoomActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1000
        private val PERMISSIONS_REQUIRED = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

    private val tvRoomId by lazy { findViewById<TextView>(R.id.tv_room_id) }
    private val tvLocalUserId by lazy { findViewById<TextView>(R.id.tv_local_user_id) }
    private val tvRemoteUserId by lazy { findViewById<TextView>(R.id.tv_remote_user_id) }
    private val localVideoContainer by lazy { findViewById<FrameLayout>(R.id.local_video_container) }

    private lateinit var roomId : String
    private lateinit var userId : String
    private lateinit var mRTCVideo : RTCVideo
    private lateinit var mRTCRoom : RTCRoom
    private val remoteUserNum = 1
    private val mShowUidArray = arrayOfNulls<String>(remoteUserNum)
//    private val mShowUidArray : MutableList<String> by lazy {
//        mutableListOf(
//            ""
//        )
//    }
    private val mUserIdTvArray by lazy {
        mutableListOf<TextView>(
            findViewById(R.id.tv_remote_user_id)
        )
    }
    private val mRemoteContainerArray by lazy {
        mutableListOf<FrameLayout>(
            findViewById(R.id.remote_video_container)
        )
    }

    //
    private var mIRTCVideoEventHandler = object : IRTCVideoEventHandler() {
        /**
         * SDK??????????????????????????????????????????????????????????????????
         */
        override fun onFirstRemoteVideoFrameDecoded(remoteStreamKey: RemoteStreamKey, frameInfo: VideoFrameInfo) {
            super.onFirstRemoteVideoFrameDecoded(remoteStreamKey, frameInfo)
            Log.d("wdw", "onFirstRemoteVideoFrame: $remoteStreamKey")
            runOnUiThread {
                setRemoteView(remoteStreamKey.roomId, remoteStreamKey.userId)
            }
        }
        /**
         * ?????????????????????????????? https://www.volcengine.com/docs/6348/70082#warncode
         */
        override fun onWarning(warn: Int) {
            super.onWarning(warn)
            Log.d("wdw", "onWarning: $warn")
        }

        /**
         * ?????????????????????????????? https://www.volcengine.com/docs/6348/70082#errorcode
         */
        override fun onError(err: Int) {
            super.onError(err)
            Log.d("wdw", "onError: $err")
        }
    }

    // ????????????????????????
    private var mIRTCRoomEventHandler = object : IRTCRoomEventHandler() {
        override fun onLeaveRoom(p0: RTCRoomStats?) {}

        override fun onRoomStateChanged(p0: String?, p1: String?, p2: Int, p3: String?) {}

        override fun onStreamStateChanged(p0: String?, p1: String?, p2: Int, p3: String?) {}

        override fun onRoomWarning(p0: Int) {}

        override fun onRoomError(p0: Int) {}

        override fun onAVSyncStateChange(p0: AVSyncState?) {}

        override fun onRoomStats(p0: RTCRoomStats?) {}

        override fun onUserJoined(userInfo: UserInfo, elapsed: Int) {
            Log.d("wdw", "onUserJoined")
        }

        override fun onUserLeave(uid: String, reason: Int) {
            Log.d("wdw", "onUserLeave: $uid")
            runOnUiThread { removeRemoteView(uid) }
        }

        override fun onTokenWillExpire() {}

        override fun onUserPublishStream(p0: String?, p1: MediaStreamType?) {}

        override fun onUserUnpublishStream(p0: String?, p1: MediaStreamType?, p2: StreamRemoveReason?) {}

        override fun onUserPublishScreen(p0: String?, p1: MediaStreamType?) {}

        override fun onUserUnpublishScreen(p0: String?, p1: MediaStreamType?, p2: StreamRemoveReason?) {}

        override fun onLocalStreamStats(p0: LocalStreamStats?) {}

        override fun onRemoteStreamStats(p0: RemoteStreamStats?) {}

        override fun onStreamRemove(p0: RTCStream?, p1: StreamRemoveReason?) {}

        override fun onStreamAdd(p0: RTCStream?) {}

        override fun onStreamSubscribed(p0: Int, p1: String?, p2: SubscribeConfig?) {}

        override fun onStreamPublishSuccess(p0: String?, p1: Boolean) {}

        override fun onRoomMessageReceived(p0: String?, p1: String?) {}

        override fun onRoomBinaryMessageReceived(p0: String?, p1: ByteBuffer?) {}

        override fun onUserMessageReceived(p0: String?, p1: String?) {}

        override fun onUserBinaryMessageReceived(p0: String?, p1: ByteBuffer?) {}

        override fun onUserMessageSendResult(p0: Long, p1: Int) {}

        override fun onRoomMessageSendResult(p0: Long, p1: Int) {}

        override fun onVideoStreamBanned(p0: String?, p1: Boolean) {}

        override fun onAudioStreamBanned(p0: String?, p1: Boolean) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        // ???????????????????????????
        if (!allPermissionsGranted(PERMISSIONS_REQUIRED)) {
            requestPermissions(this@RoomActivity, PERMISSIONS_REQUIRED, PERMISSION_REQUEST_CODE)
        }

        roomId = intent.getStringExtra("room_id")!!
        userId = intent.getStringExtra("user_id")!!

        // ?????????UI
        initUI()

        // ?????????????????????????????????
        initEngineAndJoinRoom()
    }

    private fun initUI() {
        tvRoomId.text = roomId
        tvLocalUserId.text = userId
    }

    // ??????????????????
    private fun initEngineAndJoinRoom() {
        // ????????????
         mRTCVideo = RTCVideo.createRTCVideo(context, APP_ID, mIRTCVideoEventHandler, null, null)

        // ????????????????????????
        val videoEncoderConfig = VideoEncoderConfig(360, 640, 15, 800) // todo
        mRTCVideo.setVideoEncoderConfig(videoEncoderConfig)

        // ??????????????????????????????
        setLocalRenderView()

        // ????????????????????????
        mRTCVideo.startVideoCapture()

        // ????????????????????????
        mRTCVideo.startAudioCapture()

        // ????????????
        mRTCRoom = mRTCVideo.createRTCRoom(roomId)
        mRTCRoom.setRTCRoomEventHandler(mIRTCRoomEventHandler)
        val roomConfig = RTCRoomConfig(ChannelProfile.CHANNEL_PROFILE_COMMUNICATION, true, true, true)
        val joinRoomRes = mRTCRoom.joinRoom(TOKEN, UserInfo.create(userId, ""), roomConfig)
        Log.d("wdw", "initEngineAndJoinRoom: $joinRoomRes")
    }

    private fun setLocalRenderView() {
        val renderView = TextureView(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        localVideoContainer.removeAllViews()
        localVideoContainer.addView(renderView, params)
        val videoCanvas = VideoCanvas()
        videoCanvas.renderView = renderView
        videoCanvas.uid = userId
        videoCanvas.isScreen = false
        videoCanvas.renderMode = VideoCanvas.RENDER_MODE_HIDDEN
        mRTCVideo.setLocalVideoCanvas(StreamIndex.STREAM_INDEX_MAIN, videoCanvas)
    }

    private fun setRemoteView(roomId: String, remoteUid: String) {
        var emptyIndex = -1
        for (i in mShowUidArray.indices) {
            if (mShowUidArray[i] == null && emptyIndex == -1) { // ??????????????????index
                emptyIndex = i
            } else if (mShowUidArray[i] == remoteUid) { // ???????????????2?????????user_id
                Toast.makeText(this, "????????????????????????$remoteUid, ??????????????????~", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if (emptyIndex < 0) {
            Log.e("wdw", "???????????????????????????~")
            return
        }
        mShowUidArray[emptyIndex] = remoteUid
        mUserIdTvArray[emptyIndex].text = String.format("remote_user_$emptyIndex: %s", remoteUid)
        setRemoteRenderView(roomId, remoteUid, mRemoteContainerArray[emptyIndex])
    }

    // ????????????????????????????????????
    private fun setRemoteRenderView(roomId: String, remoteUid: String, container: FrameLayout) {
        val renderView = TextureView(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        container.removeAllViews()
        container.addView(renderView, params)
        val videoCanvas = VideoCanvas()
        videoCanvas.renderView = renderView
        videoCanvas.roomId = roomId
        videoCanvas.uid = remoteUid
        videoCanvas.isScreen = false
        videoCanvas.renderMode = VideoCanvas.RENDER_MODE_HIDDEN
        mRTCVideo.setRemoteVideoCanvas(remoteUid, StreamIndex.STREAM_INDEX_MAIN, videoCanvas)
    }

    // ??????????????????????????????
    private fun removeRemoteView(remoteUid: String) {
        for (i in mShowUidArray.indices) {
            if (remoteUid == mShowUidArray[i]) {
                mShowUidArray[i] = null
                mUserIdTvArray[i].text = null
                mRemoteContainerArray[i].removeAllViews()
            }
        }
    }

//    private fun onSwitchCameraClick() {
//        // ????????????/????????????????????????????????????????????????
//        if (mCameraID == CameraId.CAMERA_ID_FRONT) {
//            mCameraID = CameraId.CAMERA_ID_BACK
//        } else {
//            mCameraID = CameraId.CAMERA_ID_FRONT
//        }
//        mRTCVideo.switchCamera(mCameraID)
//    }
//
//    private fun updateSpeakerStatus() {
//        mIsSpeakerPhone = !mIsSpeakerPhone
//        // ??????????????????????????????????????????
//        mRTCVideo.audioRoute =
//            if (mIsSpeakerPhone) AudioRoute.AUDIO_ROUTE_SPEAKERPHONE else AudioRoute.AUDIO_ROUTE_EARPIECE
//        mSpeakerIv.setImageResource(if (mIsSpeakerPhone) R.drawable.speaker_on else R.drawable.speaker_off)
//    }
//
//    private fun updateLocalAudioStatus() {
//        mIsMuteAudio = !mIsMuteAudio
//        // ??????/????????????????????????
//        if (mIsMuteAudio) {
//            mRTCRoom.unpublishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_AUDIO)
//        } else {
//            mRTCRoom.publishStream(MediaStreamType.RTC_MEDIA_STREAM_TYPE_AUDIO)
//        }
//        mAudioIv.setImageResource(if (mIsMuteAudio) R.drawable.mute_audio else R.drawable.normal_audio)
//    }
//
//    private fun updateLocalVideoStatus() {
//        mIsMuteVideo = !mIsMuteVideo
//        if (mIsMuteVideo) {
//            // ??????????????????
//            mRTCVideo.stopVideoCapture()
//        } else {
//            // ??????????????????
//            mRTCVideo.startVideoCapture()
//        }
//        mVideoIv.setImageResource(if (mIsMuteVideo) R.drawable.mute_video else R.drawable.normal_video)
//    }

    // ?????????????????????????????????
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted(PERMISSIONS_REQUIRED)) {
                Toast.makeText(this, "??????????????????~", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "??????????????????~???????????????~", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
        // ????????????
        if (mRTCRoom != null) {
            mRTCRoom!!.leaveRoom()
            mRTCRoom!!.destroy()
        }
//        mRTCRoom = null
        // ????????????
        RTCVideo.destroyRTCVideo()
//        mIRTCVideoEventHandler = null
//        mIRTCRoomEventHandler = null
//        mRTCVideo = null
    }
}