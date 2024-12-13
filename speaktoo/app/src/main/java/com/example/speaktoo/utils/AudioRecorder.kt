package com.example.speaktoo.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile

class AudioRecorder(
    private val outputFile: File,
    private val context: Context
) {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private val sampleRate = 16000 // 16 kHz sample rate
    private val channels = AudioFormat.CHANNEL_IN_MONO
    private val encoding = AudioFormat.ENCODING_PCM_16BIT
    private val bitDepth = 16 // 16 bits per sample

    companion object {
        const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    fun startRecording() {
        // Check for RECORD_AUDIO permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestAudioPermission()
            return
        }

        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channels, encoding)
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            showToast("Invalid buffer size")
            return
        }

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channels,
            encoding,
            bufferSize
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            showToast("Failed to initialize AudioRecord")
            return
        }

        audioRecord?.startRecording()
        isRecording = true

        // Start a background thread to save PCM data to WAV
        Thread {
            writeWavFile(bufferSize)
        }.start()
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    private fun writeWavFile(bufferSize: Int) {
        val data = ByteArray(bufferSize)
        FileOutputStream(outputFile).use { outputStream ->
            try {
                // Write WAV file header
                writeWavHeader(outputStream, sampleRate, 1, bitDepth)

                while (isRecording) {
                    val read = audioRecord?.read(data, 0, data.size) ?: 0
                    if (read > 0) {
                        outputStream.write(data, 0, read)
                    }
                }

                // Update WAV file header with actual file size
                updateWavHeader(outputFile)
            } catch (e: IOException) {
                e.printStackTrace()
                showToast("Error writing audio file")
            }
        }
    }

    private fun writeWavHeader(out: FileOutputStream, sampleRate: Int, channels: Int, bitDepth: Int) {
        val byteRate = sampleRate * channels * bitDepth / 8
        val header = ByteArray(44)

        // RIFF/WAV file header
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        // Placeholder for file size
        header[4] = 0
        header[5] = 0
        header[6] = 0
        header[7] = 0
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // PCM chunk size
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // Audio format (PCM)
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * bitDepth / 8).toByte() // Block align
        header[33] = 0
        header[34] = bitDepth.toByte() // Bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        // Placeholder for data chunk size
        header[40] = 0
        header[41] = 0
        header[42] = 0
        header[43] = 0

        out.write(header, 0, 44)
    }

    private fun updateWavHeader(wavFile: File) {
        val fileSize = wavFile.length().toInt()
        RandomAccessFile(wavFile, "rw").use { raf ->
            // Update file size
            raf.seek(4)
            raf.writeInt(fileSize - 8)
            // Update data chunk size
            raf.seek(40)
            raf.writeInt(fileSize - 44)
        }
    }

    private fun requestAudioPermission() {
        if (context is Activity) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            showToast("Unable to request permissions from a non-activity context")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}