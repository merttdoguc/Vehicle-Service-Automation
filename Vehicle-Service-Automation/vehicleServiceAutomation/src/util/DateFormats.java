package util;

import java.time.format.DateTimeFormatter;

/** Uygulama genelinde kullanılan tarih/saat biçimleri. */
public final class DateFormats {

    public static final DateTimeFormatter TARIH_SAAT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final DateTimeFormatter TARIH_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static final DateTimeFormatter SAAT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private DateFormats() {
    }
}
