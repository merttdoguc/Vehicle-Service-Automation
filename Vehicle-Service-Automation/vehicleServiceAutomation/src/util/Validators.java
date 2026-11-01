package util;

import java.util.Locale;

import util.Validators;

/** Plaka, servis tarihi ve yakıt tipi için doğrulama / normalleştirme yardımcıları. */
public final class Validators {

    private Validators() {
    }

    public static String normalizePlaka(String p) {
        if (p == null) return "";
        p = p.trim().replaceAll("\\s+", " ");
        return p.toUpperCase(Locale.forLanguageTag("tr-TR"));
    }

    public static boolean isPlakaGecerli(String plaka) {
        if (plaka == null) return false;
        return plaka.matches("^(0[1-9]|[1-7][0-9]|8[01])\\s[A-Z]{1,3}\\s\\d{2,4}$");
    }

    public static boolean isGecerliServisTarihi(String tarih) {
        if (tarih == null) return false;
        if (!tarih.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) return false;
        try {
            int gun = Integer.parseInt(tarih.substring(0, 2));
            int ay = Integer.parseInt(tarih.substring(3, 5));
            if (ay < 1 || ay > 12) return false;
            if (gun < 1) return false;
            if (ay == 2) return gun <= 28;
            return gun <= 31;
        } catch (Exception e) {
            return false;
        }
    }

    public static String normalizeYakitTipi(String s) {
        if (s == null) return "";
        String t = s.trim();
        t = t.replaceAll("\\s*/\\s*", "/"); // Benzin / LPG -> Benzin/LPG
        t = t.replaceAll("\\s+", " ");
        t = t.toUpperCase(Locale.forLanguageTag("tr-TR"));
        // Türkçe İ sorununu ASCII'ye yaklaştır
        t = t.replace('İ', 'I');
        return t;
    }

    public static boolean isYakitTipiGecerli(String s) {
        String t = normalizeYakitTipi(s);
        return t.equals("BENZIN") || t.equals("DIZEL") || t.equals("ELEKTRIK") || t.equals("BENZIN/LPG");
    }

    public static String canonicalYakitTipi(String s) {
        String t = normalizeYakitTipi(s);
        return switch (t) {
            case "BENZIN" -> "Benzin";
            case "DIZEL" -> "Dizel";
            case "ELEKTRIK" -> "Elektrik";
            case "BENZIN/LPG" -> "Benzin/LPG";
            default -> s.trim();
        };
    }
}
