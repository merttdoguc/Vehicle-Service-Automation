package service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import model.Arac;
import model.Musteri;
import model.Randevu;
import service.FaturaService;

import static util.DateFormats.TARIH_SAAT_FORMAT;

/** Fatura dosyası oluşturma ve servis ücreti hesaplama. */
public final class FaturaService {

    private FaturaService() {
    }

    public static String olusturFaturaDosyaAdi(Randevu r) {
        Musteri m = r.getMusteri();
        String base;
        if (m != null) {
            String ad = m.getAd() != null ? m.getAd().trim() : "";
            String soyad = m.getSoyad() != null ? m.getSoyad().trim() : "";
            base = (ad + "_" + soyad).trim();
            if (base.isEmpty()) base = "Musteri_" + r.getRandevuID();
        } else {
            base = "Musteri_" + r.getRandevuID();
        }
        base = base.replaceAll("\\s+", "_");
        return base + ".txt";
    }

    public static void faturaDosyasiYaz(Randevu secili, String dosyaAdi, double ucret) throws IOException {
        Musteri m = secili.getMusteri();
        Arac a = secili.getArac();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosyaAdi))) {
            bw.write("=========== FATURA ===========");
            bw.newLine();
            bw.write("Fatura Tarihi : " + LocalDateTime.now().format(TARIH_SAAT_FORMAT));
            bw.newLine();
            bw.write("--------------------------------");
            bw.newLine();

            bw.write("MÜŞTERİ BİLGİLERİ");
            bw.newLine();
            if (m != null) {
                bw.write("Ad Soyad  : " + m.getAd() + " " + m.getSoyad());
                bw.newLine();
                bw.write("Telefon   : " + m.getTelefon());
                bw.newLine();
                bw.write("E-posta   : " + m.getEmail());
                bw.newLine();
            } else {
                bw.write("Müşteri bilgisi bulunamadı.");
                bw.newLine();
            }

            bw.write("--------------------------------");
            bw.newLine();
            bw.write("ARAÇ BİLGİLERİ");
            bw.newLine();
            if (a != null) {
                bw.write("Plaka     : " + a.getPlaka());
                bw.newLine();
                bw.write("Marka     : " + a.getMarka());
                bw.newLine();
                bw.write("Model     : " + a.getModel());
                bw.newLine();
                bw.write("Yakıt Tipi: " + a.getYakitTipi());
                bw.newLine();
            } else {
                bw.write("Araç bilgisi bulunamadı.");
                bw.newLine();
            }

            bw.write("--------------------------------");
            bw.newLine();
            bw.write("İŞLEM BİLGİLERİ");
            bw.newLine();
            bw.write("Servis Türü      : " + secili.getServisTuru());
            bw.newLine();
            bw.write("Randevu Tarihi   : " + secili.getTarihSaat().format(TARIH_SAAT_FORMAT));
            bw.newLine();
            bw.write("Durum            : " + secili.getDurum());
            bw.newLine();

            bw.write("--------------------------------");
            bw.newLine();
            bw.write(String.format("Toplam Tutar     : %.2f TL", ucret));
            bw.newLine();
            bw.write("=========== TEŞEKKÜR EDERİZ ===========");
            bw.newLine();
        }
    }

    public static double servisUcreti(String servisTuru) {
        if (servisTuru == null) return 0;
        return switch (servisTuru) {
            case "Periyodik Bakım" -> 3000.0;
            case "Yağ Değişimi" -> 1000.0;
            case "Fren Bakımı" -> 2500.0;
            case "Lastik Değişimi" -> 1500.0;
            case "Genel Kontrol" -> 800.0;
            default -> 0.0;
        };
    }
}
