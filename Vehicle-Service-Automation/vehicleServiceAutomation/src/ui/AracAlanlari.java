package ui;

import java.time.Year;

import static ui.UiFactory.showAlert;
import static util.Validators.canonicalYakitTipi;
import static util.Validators.isYakitTipiGecerli;

/**
 * Araç formundaki yakıt tipi, üretim yılı ve kilometre alanlarının doğrulanmış hali.
 */
record AracAlanlari(String yakitTipi, int uretimYili, int km) {

    /**
     * Alanları doğrular. Hatalıysa kullanıcıya uyarı gösterip {@code null} döner.
     * Sayısal olmayan değerlerde {@link NumberFormatException} fırlatır; çağıran taraf yakalar.
     */
    static AracAlanlari dogrula(String yakitTipiRaw, String uretimYiliS, String kmS) {
        if (yakitTipiRaw.isEmpty()) {
            showAlert("Yakıt Tipi Hatası", "Yakıt tipi boş olamaz.\nKabul edilenler: Benzin, Dizel, Elektrik, Benzin/LPG");
            return null;
        }
        if (!isYakitTipiGecerli(yakitTipiRaw)) {
            showAlert("Yakıt Tipi Hatası", "Yakıt tipi sadece şu değerlerden biri olabilir:\nBenzin, Dizel, Elektrik, Benzin/LPG");
            return null;
        }
        String yakitTipi = canonicalYakitTipi(yakitTipiRaw);

        int uretimYili = 0;
        int km = 0;

        if (!uretimYiliS.isEmpty()) uretimYili = Integer.parseInt(uretimYiliS);
        if (!kmS.isEmpty()) km = Integer.parseInt(kmS);

        int mevcutYil = Year.now().getValue();
        if (uretimYili != 0 && (uretimYili < 1900 || uretimYili > mevcutYil + 1)) {
            showAlert("Üretim Yılı Hatası", "Geçerli bir üretim yılı giriniz (1900 - " + (mevcutYil + 1) + ").");
            return null;
        }

        if (km < 0 || km > 9_999_999) {
            showAlert("Kilometre Hatası", "Kilometre 0 ile 9.999.999 arasında olmalıdır.");
            return null;
        }

        return new AracAlanlari(yakitTipi, uretimYili, km);
    }
}
