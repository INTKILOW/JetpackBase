package top.intkilow.architecture.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import java.io.File
import java.lang.ref.WeakReference
import kotlin.math.log10

class AudioCore private constructor() {
    // 录音实例
    private var mMediaRecorder: MediaRecorder? = null

    // 播放实例
    private var mMediaPlayer: MediaPlayer? = null

    // 录音路径
    private var currentFilePath: String? = null

    private var weakContext: WeakReference<Context>? = null

    // 分贝回调线程
    private var readDbThread: Thread? = null

    fun init(context: Context) {
        weakContext = WeakReference<Context>(context)
    }

    /**
     * 开始录音
     * 调用此方法前先判断是否有录音权限 ！！
     * context 用于获取录音路径
     */
    fun startRecord(
        onProgress: (db: Double) -> Unit = {},
        fileName: String = "${System.currentTimeMillis()}.m4a"
    ) {
        weakContext?.get()?.let { context ->

            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                throw Exception("no android.permission.RECORD_AUDIO")
            }

            if (null != currentFilePath) {
                stopRecord()
            }

            mMediaRecorder = MediaRecorder()

            /* ②setAudioSource/setVideoSource */
            mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            // 设置保存录音路径
            currentFilePath =
                context.filesDir.path + File.separator + "audio" + File.separator + fileName

            mMediaRecorder?.setOutputFile(currentFilePath)
            mMediaRecorder?.prepare()


            readDbThread = Thread {
                var start = System.currentTimeMillis()

                while (!Thread.currentThread().isInterrupted) {
                    // 间隔取样时间
                    if (System.currentTimeMillis() - start > 100L) {
                        val maxAmplitude: Double = (mMediaRecorder?.maxAmplitude ?: 0) * 1.0
                        val db = if (maxAmplitude > 1) {
                            log10(maxAmplitude)
                        } else {
                            0.0
                        }
                        // 回调分贝
                        onProgress(db)
                        start = System.currentTimeMillis()
                    }
                }
            }
            readDbThread?.start()
            mMediaRecorder?.start()


        }
    }

    /**
     * 停止录音
     */
    fun stopRecord(): String {
        readDbThread?.interrupt()
        readDbThread = null

        mMediaRecorder?.stop()


        mMediaRecorder?.release()
        mMediaRecorder = null

        val path = currentFilePath ?: ""
        currentFilePath = null

        return path
    }


    fun play(
        url: String,
        // 毫秒
        onDuration: (Int) -> Unit = {},
        onComplete:()->Unit={},
        onError: (Int, Int) -> Unit = { _: Int, _: Int -> }
    ) {
        mMediaPlayer = MediaPlayer()
        mMediaPlayer?.setDataSource(url)
        mMediaPlayer?.setOnPreparedListener { mp ->
            onDuration(mp.duration)
            // 开始播放了
            mp.start()
        }
        mMediaPlayer?.setOnErrorListener { _, what, extra ->
            // 播放出错了
            stop()
            onError(what, extra)
            false
        }
        mMediaPlayer?.setOnCompletionListener {
            // 播放完成
            stop()
            onComplete()
        }
        mMediaPlayer?.prepareAsync()
    }

    fun stop() {
        mMediaPlayer?.stop()
        mMediaPlayer?.release()
        mMediaPlayer = null
    }

    fun release(){
        stopRecord()
        stop()
    }


    companion object {
        val instance: AudioCore by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioCore()
        }
    }

}