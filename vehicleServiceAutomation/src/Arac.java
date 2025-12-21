import java.io.Serializable;

public class Arac implements Serializable {

    private String plaka;
    private String marka;
    private String model;
    private int uretimYili;
    private int kilometre;
    private String sonServisTarihi;
    private String sasiNo;
    private String yakitTipi;

    // Constructor
    public Arac(String plaka, String marka, String model,
                int uretimYili, int kilometre, String sonServisTarihi,
                String sasiNo, String yakitTipi) {
        this.plaka = plaka;
        this.marka = marka;
        this.model = model;
        this.uretimYili = uretimYili;
        this.kilometre = kilometre;
        this.sonServisTarihi = sonServisTarihi;
        this.sasiNo = sasiNo;
        this.yakitTipi = yakitTipi;
    }

    // --- .TXT İŞLEMLERİ İÇİN YARDIMCI METOTLAR (YENİ) ---

    /**
     * Araç bilgilerini .txt dosyasına yazılacak CSV formatına dönüştürür.
     */
    public String toTxtFormat() {
        return plaka + ";" + marka + ";" + model + ";" + uretimYili + ";" +
                kilometre + ";" + sonServisTarihi + ";" + sasiNo + ";" + yakitTipi;
    }

    /**
     * .txt dosyasından okunan satırı Arac nesnesine dönüştürür.
     */
    public static Arac fromTxtFormat(String satir) {
        String[] v = satir.split(";");
        // uretimYili (v[3]) ve kilometre (v[4]) int olduğu için parse ediyoruz.
        return new Arac(
                v[0], v[1], v[2],
                Integer.parseInt(v[3]),
                Integer.parseInt(v[4]),
                v[5], v[6], v[7]
        );
    }

    // Getter Setter Metotlar
    public String getPlaka() {
        return plaka;
    }

    public void setPlaka(String plaka) {
        this.plaka = plaka;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getUretimYili() {
        return uretimYili;
    }

    public void setUretimYili(int uretimYili) {
        this.uretimYili = uretimYili;
    }

    public int getKilometre() {
        return kilometre;
    }

    public void setKilometre(int kilometre) {
        this.kilometre = kilometre;
    }

    public String getSonServisTarihi() {
        return sonServisTarihi;
    }

    public void setSonServisTarihi(String sonServisTarihi) {
        this.sonServisTarihi = sonServisTarihi;
    }

    public String getSasiNo() {
        return sasiNo;
    }

    public void setSasiNo(String sasiNo) {
        this.sasiNo = sasiNo;
    }

    public String getYakitTipi() {
        return yakitTipi;
    }

    public void setYakitTipi(String yakitTipi) {
        this.yakitTipi = yakitTipi;
    }
}