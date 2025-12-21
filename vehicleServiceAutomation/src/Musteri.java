import java.io.Serializable;
import java.util.LinkedList;

public class Musteri implements Serializable {

    private int musteriID;
    private String ad;
    private String soyad;
    private String telefon;
    private String email;
    private String musteriKodu; // Her müşterinin özel bir 5 haneli kodu var

    private LinkedList<Arac> sahipOlunanAraclar;

    public Musteri(int musteriID, String ad, String soyad,
                   String telefon, String email, String musteriKodu) {
        this.musteriID = musteriID;
        this.ad = ad;
        this.soyad = soyad;
        this.telefon = telefon;
        this.email = email;
        this.musteriKodu = musteriKodu;
        this.sahipOlunanAraclar = new LinkedList<>();
    }

    // --- .TXT İŞLEMLERİ İÇİN YARDIMCI METOTLAR (YENİ) ---

    /**
     * Müşteri bilgilerini .txt dosyasına yazılacak formata (CSV) dönüştürür.
     */
    public String toTxtFormat() {
        return musteriID + ";" + ad + ";" + soyad + ";" + telefon + ";" + email + ";" + musteriKodu;
    }

    /**
     * .txt dosyasından okunan satırı Musteri nesnesine dönüştürür.
     */
    public static Musteri fromTxtFormat(String satir) {
        String[] v = satir.split(";");
        return new Musteri(Integer.parseInt(v[0]), v[1], v[2], v[3], v[4], v[5]);
    }

    // Getter Setter Metotlar
    public int getMusteriID() {
        return musteriID;
    }

    public void setMusteriID(int musteriID) {
        this.musteriID = musteriID;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LinkedList<Arac> getSahipOlunanAraclar() {
        return sahipOlunanAraclar;
    }

    public void aracEkle(Arac yeniArac) {
        this.sahipOlunanAraclar.add(yeniArac);
    }

    public String getMusteriKodu() {
        return musteriKodu;
    }

    public void setMusteriKodu(String musteriKodu) {
        this.musteriKodu = musteriKodu;
    }
}