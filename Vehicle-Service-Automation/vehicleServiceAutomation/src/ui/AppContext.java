package ui;

import javafx.stage.Stage;
import model.Musteri;
import model.Randevu;
import service.ServisYonetimSistemi;

/**
 * Ekranlar arasında paylaşılan durum (servis katmanı, ID sayaçları) ve ekran geçişleri.
 */
public class AppContext {

    private final ServisYonetimSistemi servis = new ServisYonetimSistemi();

    // Otomatik ID sayaçları
    private int nextMusteriID = 1;
    private int nextRandevuID = 1;

    ServisYonetimSistemi servis() {
        return servis;
    }

    int yeniMusteriID() {
        return nextMusteriID++;
    }

    int yeniRandevuID() {
        return nextRandevuID++;
    }

    /** Dosyadan yüklenen kayıtlara göre sıradaki ID'leri ayarlar. */
    public void idSayaclariniGuncelle() {
        for (Musteri m : servis.getMusteriListesi()) {
            if (m.getMusteriID() >= nextMusteriID) nextMusteriID = m.getMusteriID() + 1;
        }
        for (Randevu r : servis.getRandevuListesi()) {
            if (r.getRandevuID() >= nextRandevuID) nextRandevuID = r.getRandevuID() + 1;
        }
    }

    public void showLoginScreen(Stage stage) {
        new LoginScreen(this).show(stage);
    }

    public void showCustomerRegisterScreen(Stage stage) {
        new CustomerRegisterScreen(this).show(stage);
    }

    public void showCustomerPortalScreen(Stage stage, Musteri musteri) {
        new CustomerPortalScreen(this).show(stage, musteri);
    }

    public void showStaffScreen(Stage stage) {
        new StaffScreen(this).show(stage);
    }
}
