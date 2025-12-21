import java.time.LocalDateTime;
import java.io.Serializable;

public class Randevu implements  Serializable {

    private int randevuID;
    private Musteri musteri;    // Müşteri sınıfından bir nesne
    private Arac arac;          // Araç sınıfından bir nesne
    private LocalDateTime tarihSaat;
    private String servisTuru;
    private String durum;       // Onay durumu

    // Constructor
    public Randevu(int randevuID, Musteri musteri, Arac arac,
                   LocalDateTime tarihSaat, String servisTuru, String durum) {
        this.randevuID = randevuID;
        this.musteri = musteri;
        this.arac = arac;
        this.tarihSaat = tarihSaat;
        this.servisTuru = servisTuru;
        this.durum = durum;
    }

    // --- .TXT İŞLEMLERİ İÇİN YARDIMCI METOT (YENİ) ---

    /**
     * Randevu bilgilerini .txt dosyasına yazar.
     * Nesne referansları yerine Müşteri ID ve Araç Plaka kaydedilir.
     */
    public String toTxtFormat() {
        return randevuID + ";" +
                musteri.getMusteriID() + ";" +
                arac.getPlaka() + ";" +
                tarihSaat.toString() + ";" +
                servisTuru + ";" +
                durum;
    }

    // Getter Setter Metotlar
    public int getRandevuID() {
        return randevuID;
    }

    public void setRandevuID(int randevuID) {
        this.randevuID = randevuID;
    }

    public Musteri getMusteri() {
        return musteri;
    }

    public void setMusteri(Musteri musteri) {
        this.musteri = musteri;
    }

    public Arac getArac() {
        return arac;
    }

    public void setArac(Arac arac) {
        this.arac = arac;
    }

    public LocalDateTime getTarihSaat() {
        return tarihSaat;
    }

    public void setTarihSaat(LocalDateTime tarihSaat) {
        this.tarihSaat = tarihSaat;
    }

    public String getServisTuru() {
        return servisTuru;
    }

    public void setServisTuru(String servisTuru) {
        this.servisTuru = servisTuru;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }
}