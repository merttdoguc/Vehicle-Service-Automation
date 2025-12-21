import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Random;

public class ServisYonetimSistemi {

    private LinkedList<Musteri> musteriListesi;
    private LinkedList<Arac> aracListesi;
    private LinkedList<Randevu> randevuListesi;

    // Dosya isimleri (.txt formatında)
    private static final String MUSTERI_DOSYA = "musteriler.txt";
    private static final String ARAC_DOSYA = "araclar.txt";
    private static final String RANDEVU_DOSYA = "randevular.txt";

    public ServisYonetimSistemi() {
        musteriListesi = new LinkedList<>();
        aracListesi = new LinkedList<>();
        randevuListesi = new LinkedList<>();
        verileriYukle();
    }

    // Getter Metotlar
    public LinkedList<Musteri> getMusteriListesi() {
        return musteriListesi;
    }

    public LinkedList<Arac> getAracListesi() {
        return aracListesi;
    }

    public LinkedList<Randevu> getRandevuListesi() {
        return randevuListesi;
    }

    // Müşteri İşlemleri
    public void musteriEkle(Musteri m) {
        if (m == null) return;
        musteriListesi.add(m);
        verileriKaydet();
    }

    public void musteriListesiniGuncelle() { musterileriKaydet(); }

    public Musteri musteriTelefonlaBul(String telefon) {
        if (telefon == null) return null;
        for (Musteri m : musteriListesi) {
            if (telefon.equals(m.getTelefon())) return m;
        }
        return null;
    }

    public Musteri musteriKodlaBul(String musteriKodu) {
        if (musteriKodu == null) return null;
        String arananKod = musteriKodu.trim();
        for (Musteri m : musteriListesi) {
            if (m.getMusteriKodu() != null && m.getMusteriKodu().trim().equals(arananKod)) return m;
        }
        return null;
    }

    public String yeniMusteriKoduUret() {
        Random rnd = new Random();
        while (true) {
            String kod = String.valueOf(10000 + rnd.nextInt(90000));
            if (musteriKodlaBul(kod) == null) return kod;
        }
    }

    // Araç İşlemleri

    // Aracı Müşteriye Bağlayan Metot
    public void musteriyeAracEkle(Musteri m, Arac a) {
        if (m == null || a == null) return;
        m.aracEkle(a); // Müşterinin listesine ekle
        if (!aracListesi.contains(a)) aracListesi.add(a); // Genel listeye ekle
        verileriKaydet(); // Dosyaya (müşterinin yanına) yaz
    }

    public void aracEkle(Arac a) {
        if (a == null) return;
        if (!aracListesi.contains(a)) aracListesi.add(a);
        verileriKaydet();
    }

    public void aracListesiniGuncelle() { araclariKaydet(); }

    public Arac aracAra(String plaka) {
        if (plaka == null) return null;
        for (Arac a : aracListesi) {
            if (a.getPlaka() != null && a.getPlaka().equalsIgnoreCase(plaka)) return a;
        }
        return null;
    }

    // Randevu İşlemleri
    public boolean randevuEkle(Randevu r) {
        if (r == null) return false;
        for (Randevu mevcut : randevuListesi) {
            if (mevcut.getArac().getPlaka().equalsIgnoreCase(r.getArac().getPlaka()) &&
                    mevcut.getTarihSaat().equals(r.getTarihSaat())) return false;
        }
        randevuListesi.add(r);
        randevulariKaydet();
        return true;
    }

    public boolean randevuSil(int randevuID) {
        boolean silindi = randevuListesi.removeIf(r -> r.getRandevuID() == randevuID);
        if (silindi) randevulariKaydet();
        return silindi;
    }

    public void randevuListesiniGuncelle() { randevulariKaydet(); }

    // Kaydetme ve Yükleme İşlemleri
    public void verileriKaydet() {
        musterileriKaydet();
        araclariKaydet();
        randevulariKaydet();
    }

    private void verileriYukle() {
        musterileriYukle();
        araclariYukle();
        randevulariYukle();
    }

    private void musterileriKaydet() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(MUSTERI_DOSYA), java.nio.charset.StandardCharsets.UTF_8))) {
            for (Musteri m : musteriListesi) {
                StringBuilder sb = new StringBuilder();
                sb.append(m.getMusteriID()).append(";")
                        .append(escapeNull(m.getAd())).append(";")
                        .append(escapeNull(m.getSoyad())).append(";")
                        .append(escapeNull(m.getTelefon())).append(";")
                        .append(escapeNull(m.getEmail())).append(";")
                        .append(escapeNull(m.getMusteriKodu())).append(";");

                // Müşterinin araçlarını satırın sonuna ekliyoruz
                if (m.getSahipOlunanAraclar() != null && !m.getSahipOlunanAraclar().isEmpty()) {
                    for (int i = 0; i < m.getSahipOlunanAraclar().size(); i++) {
                        Arac a = m.getSahipOlunanAraclar().get(i);
                        sb.append(a.getPlaka()).append(",").append(a.getMarka()).append(",")
                                .append(a.getModel()).append(",").append(a.getUretimYili()).append(",")
                                .append(a.getKilometre()).append(",").append(a.getSonServisTarihi()).append(",")
                                .append(a.getSasiNo()).append(",").append(a.getYakitTipi());
                        if (i < m.getSahipOlunanAraclar().size() - 1) sb.append("#");
                    }
                }
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException e) { System.err.println("Kayıt hatası!"); }
    }

    private void araclariKaydet() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARAC_DOSYA))) {
            for (Arac a : aracListesi) {
                pw.println(a.getPlaka() + ";" + a.getMarka() + ";" + a.getModel() + ";" + a.getUretimYili() + ";" +
                        a.getKilometre() + ";" + a.getSonServisTarihi() + ";" + a.getSasiNo() + ";" + a.getYakitTipi());
            }
        } catch (IOException e) { }
    }

    private void randevulariKaydet() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(RANDEVU_DOSYA))) {
            for (Randevu r : randevuListesi) {
                pw.println(r.getRandevuID() + ";" + r.getMusteri().getMusteriID() + ";" +
                        r.getArac().getPlaka() + ";" + r.getTarihSaat().toString() + ";" +
                        r.getServisTuru() + ";" + r.getDurum());
            }
        } catch (IOException e) { }
    }

    private void musterileriYukle() {
        File f = new File(MUSTERI_DOSYA);
        if (!f.exists()) return;
        musteriListesi.clear();
        aracListesi.clear();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), java.nio.charset.StandardCharsets.UTF_8))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                if (satir.trim().isEmpty()) continue;
                String[] v = satir.split(";", -1);
                if (v.length >= 6) {
                    Musteri m = new Musteri(Integer.parseInt(v[0].trim()), unescapeNull(v[1]),
                            unescapeNull(v[2]), unescapeNull(v[3]),
                            unescapeNull(v[4]), unescapeNull(v[5]));
                    if (v.length >= 7 && !v[6].trim().isEmpty()) {
                        String[] araclar = v[6].split("#");
                        for (String as : araclar) {
                            String[] aP = as.split(",");
                            if (aP.length >= 8) {
                                Arac ar = new Arac(aP[0], aP[1], aP[2], Integer.parseInt(aP[3]),
                                        Integer.parseInt(aP[4]), aP[5], aP[6], aP[7]);
                                m.aracEkle(ar);
                                if (aracAra(ar.getPlaka()) == null) aracListesi.add(ar);
                            }
                        }
                    }
                    musteriListesi.add(m);
                }
            }
        } catch (Exception e) { }
    }

    private void araclariYukle() {
        File f = new File(ARAC_DOSYA);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] v = satir.split(";");
                if (v.length >= 8 && aracAra(v[0]) == null) {
                    aracListesi.add(new Arac(v[0], v[1], v[2], Integer.parseInt(v[3].trim()),
                            Integer.parseInt(v[4].trim()), v[5], v[6], v[7]));
                }
            }
        } catch (Exception e) { }
    }

    private void randevulariYukle() {
        File f = new File(RANDEVU_DOSYA);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] v = satir.split(";");
                Musteri m = null;
                for (Musteri mus : musteriListesi) if (mus.getMusteriID() == Integer.parseInt(v[1])) m = mus;
                Arac a = aracAra(v[2]);
                if (m != null && a != null) {
                    randevuListesi.add(new Randevu(Integer.parseInt(v[0]), m, a, LocalDateTime.parse(v[3]), v[4], v[5]));
                }
            }
        } catch (Exception e) { }
    }

    private String escapeNull(String s) { return (s == null) ? "" : s.replace(";", " ").replace("|", " ").replace("#", " ").replace(",", " "); }
    private String unescapeNull(String s) { return (s == null || s.trim().isEmpty()) ? null : s.trim(); }
}