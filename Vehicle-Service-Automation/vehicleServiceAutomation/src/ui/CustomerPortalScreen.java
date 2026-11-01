package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import model.Arac;
import model.Musteri;
import model.Randevu;
import service.ServisYonetimSistemi;

import static ui.UiFactory.*;
import static util.Validators.*;
import static util.DateFormats.*;

/** Müşteri paneli: araç yönetimi ve randevu oluşturma. */
class CustomerPortalScreen {

    private final AppContext ctx;
    private final ServisYonetimSistemi servis;

    CustomerPortalScreen(AppContext ctx) {
        this.ctx = ctx;
        this.servis = ctx.servis();
    }

    void show(Stage stage, Musteri musteri) {
        Label lblHeader = new Label("Müşteri Paneli");
        lblHeader.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;"
        );

        Label lblWelcome = new Label("Hoş geldiniz, " + musteri.getAd() + " " + musteri.getSoyad());
        lblWelcome.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #9ca3af;" +
                        "-fx-font-size: 13;"
        );

        VBox headerBox = new VBox(4, lblHeader, lblWelcome);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(8, 0, 12, 0));

        // SOL: Araç listesi
        ObservableList<Arac> aracObs = FXCollections.observableArrayList(musteri.getSahipOlunanAraclar());

        ListView<Arac> lstAraclar = new ListView<>();
        lstAraclar.setItems(aracObs);
        lstAraclar.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Arac item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String text = item.getPlaka();
                    if (item.getMarka() != null && !item.getMarka().isEmpty()) text += " - " + item.getMarka();
                    if (item.getModel() != null && !item.getModel().isEmpty()) text += " " + item.getModel();
                    setText(text);
                }
            }
        });
        lstAraclar.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-control-inner-background: #020617;" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-selection-bar: #2563eb;" +
                        "-fx-selection-bar-text: #f9fafb;"
        );

        VBox aracCard = createCard("Kayıtlı Araçlarınız", lstAraclar);
        aracCard.setPrefWidth(340);
        aracCard.setMinWidth(300);
        aracCard.setMaxWidth(420);

        // SAĞ: Araç yönetimi formu
        TextField vPlaka = createTextField();
        TextField vMarka = createTextField();
        TextField vModel = createTextField();
        TextField vUretimYili = createTextField();
        TextField vKilometre = createTextField();
        TextField vSonServis = createTextField();
        TextField vSasiNo = createTextField();
        TextField vYakitTipi = createTextField();

        GridPane aracYonetimGrid = createFormGrid();

        int row = 0;
        addFormRow(aracYonetimGrid, row++, "Plaka", vPlaka);
        addFormRow(aracYonetimGrid, row++, "Marka", vMarka);
        addFormRow(aracYonetimGrid, row++, "Model", vModel);
        addFormRow(aracYonetimGrid, row++, "Üretim Yılı", vUretimYili);
        addFormRow(aracYonetimGrid, row++, "Kilometre", vKilometre);
        addFormRow(aracYonetimGrid, row++, "Son Servis Tarihi (gg.MM.yyyy)", vSonServis);
        addFormRow(aracYonetimGrid, row++, "Şasi No", vSasiNo);
        addFormRow(aracYonetimGrid, row++, "Yakıt Tipi (Benzin/Dizel/Elektrik/Benzin/LPG)", vYakitTipi);

        Button btnYeniAracEkle = createColoredButton("Yeni Araç Ekle", "#22c55e");
        Button btnSeciliGuncelle = createColoredButton("Seçili Aracı Güncelle", "#f97316");
        Button btnSeciliSil = createColoredButton("Seçili Aracı Sil", "#ef4444");

        HBox aracBtnBar = new HBox(10, btnYeniAracEkle, btnSeciliGuncelle, btnSeciliSil);
        aracBtnBar.setAlignment(Pos.CENTER);

        VBox aracYonetimContent = new VBox(10, aracYonetimGrid, aracBtnBar);
        VBox aracYonetimCard = createCard("Araç Yönetimi", aracYonetimContent);
        aracYonetimCard.setMaxWidth(Double.MAX_VALUE);

        Runnable aracFormTemizle = () -> {
            vPlaka.clear();
            vMarka.clear();
            vModel.clear();
            vUretimYili.clear();
            vKilometre.clear();
            vSonServis.clear();
            vSasiNo.clear();
            vYakitTipi.clear();
        };

        lstAraclar.getSelectionModel().selectedItemProperty().addListener((obs, oldV, secili) -> {
            if (secili == null) return;
            vPlaka.setText(secili.getPlaka());
            vMarka.setText(secili.getMarka());
            vModel.setText(secili.getModel());
            vUretimYili.setText(secili.getUretimYili() == 0 ? "" : String.valueOf(secili.getUretimYili()));
            vKilometre.setText(String.valueOf(secili.getKilometre()));
            vSonServis.setText(secili.getSonServisTarihi());
            vSasiNo.setText(secili.getSasiNo());
            vYakitTipi.setText(secili.getYakitTipi());
        });

        btnYeniAracEkle.setOnAction(e -> {
            try {
                String plaka = normalizePlaka(vPlaka.getText().trim());
                String marka = vMarka.getText().trim();
                String model = vModel.getText().trim();
                String uretimYiliS = vUretimYili.getText().trim();
                String kmS = vKilometre.getText().trim();
                String sonServis = vSonServis.getText().trim();
                String sasiNo = vSasiNo.getText().trim();
                String yakitTipiRaw = vYakitTipi.getText().trim();

                if (plaka.isEmpty()) {
                    showAlert("Eksik Bilgi", "Plaka boş olamaz.");
                    return;
                }

                if (!isPlakaGecerli(plaka)) {
                    showAlert("Plaka Hatası", "Plaka formatı hatalı.\nÖrn: 35 CJG 255 veya 35 KM 2453");
                    return;
                }

                if (servis.aracAra(plaka) != null) {
                    showAlert("Plaka Hatası", "Bu plaka sistemde zaten kayıtlı.");
                    return;
                }

                if (!sonServis.isEmpty() && !isGecerliServisTarihi(sonServis)) {
                    showAlert("Tarih Hatası", "Son servis tarihi gg.MM.yyyy formatında olmalı.");
                    return;
                }

                if (!sasiNo.isEmpty() && servis.sasiNoKayitliMi(sasiNo, null)) {
                    showAlert("Şasi No Hatası", "Bu şasi numarası sistemde zaten kayıtlı.");
                    return;
                }

                AracAlanlari alanlar = AracAlanlari.dogrula(yakitTipiRaw, uretimYiliS, kmS);
                if (alanlar == null) return;
                String yakitTipi = alanlar.yakitTipi();
                int uretimYili = alanlar.uretimYili();
                int km = alanlar.km();

                Arac yeni = new Arac(plaka, marka, model, uretimYili, km, sonServis, sasiNo, yakitTipi);

                // Müşteriye + sisteme ekle
                musteri.aracEkle(yeni);
                if (servis.aracAra(plaka) == null) servis.getAracListesi().add(yeni);
                servis.verileriKaydet();

                aracObs.setAll(musteri.getSahipOlunanAraclar());
                lstAraclar.getSelectionModel().select(yeni);
                showAlert("Başarılı", "Araç eklendi.");

            } catch (NumberFormatException ex) {
                showAlert("Sayı Hatası", "Üretim yılı ve kilometre sadece sayı olmalı.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Hata", "Araç eklenirken hata: " + ex.getMessage());
            }
        });

        btnSeciliGuncelle.setOnAction(e -> {
            Arac secili = lstAraclar.getSelectionModel().getSelectedItem();
            if (secili == null) {
                showAlert("Seçim Yok", "Güncellemek için listeden araç seç.");
                return;
            }

            try {
                String yeniPlaka = normalizePlaka(vPlaka.getText().trim());
                String marka = vMarka.getText().trim();
                String model = vModel.getText().trim();
                String uretimYiliS = vUretimYili.getText().trim();
                String kmS = vKilometre.getText().trim();
                String sonServis = vSonServis.getText().trim();
                String sasiNo = vSasiNo.getText().trim();
                String yakitTipiRaw = vYakitTipi.getText().trim();

                if (yeniPlaka.isEmpty()) {
                    showAlert("Eksik Bilgi", "Plaka boş olamaz.");
                    return;
                }

                if (!isPlakaGecerli(yeniPlaka)) {
                    showAlert("Plaka Hatası", "Plaka formatı hatalı.\nÖrn: 35 CJG 255 veya 35 KM 2453");
                    return;
                }

                // Plaka değişiyorsa
                if (!yeniPlaka.equalsIgnoreCase(secili.getPlaka()) && servis.aracAra(yeniPlaka) != null) {
                    showAlert("Plaka Hatası", "Bu plaka sistemde zaten kayıtlı.");
                    return;
                }

                if (!sonServis.isEmpty() && !isGecerliServisTarihi(sonServis)) {
                    showAlert("Tarih Hatası", "Son servis tarihi gg.MM.yyyy formatında olmalı.");
                    return;
                }

                // Şasi no değişiyorsa
                if (!sasiNo.isEmpty() && servis.sasiNoKayitliMi(sasiNo, secili)) {
                    showAlert("Şasi No Hatası", "Bu şasi numarası sistemde zaten kayıtlı.");
                    return;
                }

                AracAlanlari alanlar = AracAlanlari.dogrula(yakitTipiRaw, uretimYiliS, kmS);
                if (alanlar == null) return;
                String yakitTipi = alanlar.yakitTipi();
                int uretimYili = alanlar.uretimYili();
                int km = alanlar.km();

                // Aynı objeyi güncelle (randevular referans kaybetmesin)
                secili.setPlaka(yeniPlaka);
                secili.setMarka(marka);
                secili.setModel(model);
                secili.setUretimYili(uretimYili);
                secili.setKilometre(km);
                secili.setSonServisTarihi(sonServis);
                secili.setSasiNo(sasiNo);
                secili.setYakitTipi(yakitTipi);

                servis.verileriKaydet();

                aracObs.setAll(musteri.getSahipOlunanAraclar());
                // listede tekrar seç
                for (Arac a : aracObs) {
                    if (a.getPlaka() != null && a.getPlaka().equalsIgnoreCase(yeniPlaka)) {
                        lstAraclar.getSelectionModel().select(a);
                        break;
                    }
                }

                showAlert("Başarılı", "Araç güncellendi.");

            } catch (NumberFormatException ex) {
                showAlert("Sayı Hatası", "Üretim yılı ve kilometre sadece sayı olmalı.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Hata", "Araç güncellenirken hata: " + ex.getMessage());
            }
        });

        btnSeciliSil.setOnAction(e -> {
            Arac secili = lstAraclar.getSelectionModel().getSelectedItem();
            if (secili == null) {
                showAlert("Seçim Yok", "Silmek için listeden araç seç.");
                return;
            }

            // Randevusu varsa sildirme
            for (Randevu r : servis.getRandevuListesi()) {
                if (r.getArac() != null && r.getArac().getPlaka() != null &&
                        r.getArac().getPlaka().equalsIgnoreCase(secili.getPlaka())) {
                    showAlert("Silinemedi", "Bu araca ait randevu bulunduğu için silinemez.");
                    return;
                }
            }

            musteri.getSahipOlunanAraclar().remove(secili);

            // Sistem listesinden de kaldır (başka müşteride aynı plaka var mı kontrol)
            boolean baskaKullaniyorMu = false;
            for (Musteri m : servis.getMusteriListesi()) {
                if (m == musteri) continue;
                for (Arac a : m.getSahipOlunanAraclar()) {
                    if (a.getPlaka() != null && a.getPlaka().equalsIgnoreCase(secili.getPlaka())) {
                        baskaKullaniyorMu = true;
                        break;
                    }
                }
                if (baskaKullaniyorMu) break;
            }
            if (!baskaKullaniyorMu) {
                servis.getAracListesi().removeIf(a ->
                        a.getPlaka() != null && a.getPlaka().equalsIgnoreCase(secili.getPlaka()));
            }

            servis.verileriKaydet();

            aracObs.setAll(musteri.getSahipOlunanAraclar());
            lstAraclar.getSelectionModel().clearSelection();
            aracFormTemizle.run();

            showAlert("Başarılı", "Araç silindi.");
        });

        // SAĞ ALT: Randevu oluştur
        TextField cTarih = createTextField();
        TextField cSaat = createTextField();
        ComboBox<String> cServisTuru = createServisTuruCombo();

        GridPane randevuGrid = createFormGrid();

        row = 0;
        addFormRow(randevuGrid, row++, "Tarih (gg.MM.yyyy)", cTarih);
        addFormRow(randevuGrid, row++, "Saat (HH:mm)", cSaat);
        addFormRow(randevuGrid, row++, "Servis Türü", cServisTuru);

        VBox randevuCard = createCard("Randevu Oluştur", randevuGrid);
        randevuCard.setMaxWidth(Double.MAX_VALUE);

        Button btnRandevuOlustur = createColoredButton("Randevu Oluştur", "#2563eb");
        Button btnGeri = createColoredButton("Oturumu Kapat", "#ef4444");

        HBox buttonBar = new HBox(10, btnRandevuOlustur, btnGeri);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10, 0, 0, 0));

        VBox sagPanel = new VBox(12, aracYonetimCard, randevuCard, buttonBar);
        sagPanel.setMaxWidth(Double.MAX_VALUE);

        // ORTALAMA FIX: Tam ekranda küçük kalmasın
        HBox center = new HBox(20, aracCard, sagPanel);
        center.setPadding(new Insets(8, 0, 0, 0));
        HBox.setHgrow(sagPanel, Priority.ALWAYS);

        StackPane centerWrap = new StackPane(center);
        centerWrap.setAlignment(Pos.TOP_CENTER);
        centerWrap.setPadding(new Insets(8, 0, 0, 0));

        VBox mainBox = new VBox(8, headerBox, centerWrap);
        mainBox.setAlignment(Pos.TOP_CENTER);
        mainBox.setPadding(new Insets(16));
        mainBox.setStyle("-fx-background-color: #020617;");
        mainBox.setFillWidth(true);
        mainBox.setMaxWidth(Double.MAX_VALUE);

        BorderPane content = new BorderPane();
        content.setCenter(mainBox);
        content.setStyle("-fx-background-color: #020617;");

        BorderPane root = createWindowChrome(stage, content, "VivaCar Oto Servis - Müşteri Paneli");

        Scene scene = new Scene(root, 1100, 650);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        btnGeri.setOnAction(e -> {
            if (confirmLogout("Hesabınızdan çıkış yapmak istediğinize emin misiniz?")) {
                ctx.showLoginScreen(stage);
            }
        });

        btnRandevuOlustur.setOnAction(e -> {
            try {
                Arac seciliArac = lstAraclar.getSelectionModel().getSelectedItem();
                if (seciliArac == null) {
                    showAlert("Seçim Yok", "Lütfen randevu için listeden bir araç seçin.");
                    return;
                }

                String tarihStr = cTarih.getText().trim();
                String saatStr = cSaat.getText().trim();
                String servisTuru = cServisTuru.getValue();

                if (tarihStr.isEmpty() || saatStr.isEmpty()) {
                    showAlert("Eksik Bilgi", "Tarih ve Saat alanları boş olamaz.");
                    return;
                }

                LocalDateTime tarihSaat;
                try {
                    tarihSaat = LocalDateTime.parse(tarihStr + " " + saatStr, TARIH_SAAT_FORMAT);
                } catch (DateTimeParseException ex) {
                    showAlert("Tarih/Saat Hatası", "Tarih: gg.MM.yyyy, Saat: HH:mm formatında olmalı.");
                    return;
                }

                if (tarihSaat.isBefore(LocalDateTime.now())) {
                    showAlert("Geçersiz Tarih", "Geçmiş bir tarihe randevu alınamaz.");
                    return;
                }

                int yeniRandevuID = ctx.yeniRandevuID();
                String durum = "Onaylandı";

                Randevu r = new Randevu(yeniRandevuID, musteri, seciliArac, tarihSaat, servisTuru, durum);

                if (!servis.randevuEkle(r)) {
                    showAlert("Çakışma", "Bu araç için aynı tarih & saatte başka bir randevu var.");
                    return;
                }

                showAlert("Başarılı", "Randevunuz oluşturuldu. Personel ekranına düşmüştür.");
                cTarih.clear();
                cSaat.clear();
                cServisTuru.getSelectionModel().selectFirst();

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Hata", "Randevu oluşturulurken hata: " + ex.getMessage());
            }
        });
    }
}
