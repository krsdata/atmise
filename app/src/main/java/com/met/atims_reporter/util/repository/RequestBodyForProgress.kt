package com.met.atims_reporter.util.repository

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException

@Suppress("unused")
open class RequestBodyForProgress(
    private val delegate: RequestBody,
    private val debounceMills: Long = 1000
) : RequestBody() {

    private lateinit var progressCallback: ProgressCallback

    private lateinit var context: Context
    private lateinit var broadcastAction: String
    private lateinit var progressDataIntentKeyWord: String

    @Suppress("unused")
    fun requestCallback(
        progressCallback: ProgressCallback
    ): RequestBodyForProgress {
        this.progressCallback = progressCallback
        return this
    }

    @Suppress("unused")
    fun sendProgressUpdateInBroadcast(
        context: Context,
        broadcastAction: String,
        progressDataIntentKeyWord: String
    ): RequestBodyForProgress {
        this.context = context
        this.broadcastAction = broadcastAction
        this.progressDataIntentKeyWord = progressDataIntentKeyWord
        return this
    }

    private lateinit var countingSink: CountingSink

    override fun contentType(): MediaType? {
        return delegate.contentType()
    }

    override fun contentLength(): Long {
        try {
            return delegate.contentLength()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return -1
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        countingSink = CountingSink(sink)
        val bufferedSink: BufferedSink = countingSink.buffer()

        delegate.writeTo(bufferedSink)

        bufferedSink.flush()
    }

    protected inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {

        private var bytesWritten: Long = 0

        @Throws(IOException::class)
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)

            bytesWritten += byteCount
            newProgressUpdate(bytesWritten)
        }
    }

    interface ProgressCallback {
        fun progressChange(progress: Int)
    }

    private var allowedToPostUpdate = true

    private fun newProgressUpdate(done: Long) {
        val progressDone = ((done.toFloat() / contentLength()) * 100).toInt()
        if (allowedToPostUpdate || progressDone == 100) {
            blockProgressUpdate()
            if (
                this::progressCallback.isInitialized
            ) {
                progressCallback.progressChange(progressDone)
            }
            if (
                this::context.isInitialized &&
                this::broadcastAction.isInitialized &&
                this::progressDataIntentKeyWord.isInitialized
            ) {
                context.sendBroadcast(
                    Intent(broadcastAction)
                        .putExtra(
                            progressDataIntentKeyWord,
                            progressDone
                        )
                )
            }
        }
    }

    private fun blockProgressUpdate() {
        allowedToPostUpdate = false
        Handler(Looper.getMainLooper()).postDelayed(
            {
                allowedToPostUpdate = true
            },
            debounceMills
        )
    }
}