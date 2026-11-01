package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import model.Arac;
import model.Musteri;
import service.ServisYonetimSistemi;

import static ui.UiFactory.*;
import static util.Validators.*;

/** Yeni müşteri ve ilk araç kaydı ekranı. */
class CustomerRegisterScreen {

    private final AppContext ctx;
    private final ServisYonetimSistemi servis;

    CustomerRegisterScreen(AppContext ctx) {
        this.ctx = ctx;
        this.servis = ctx.servis();
    }

    void show(Stage stage) {
        Label lblHeader = new Label("Yeni Müşteri Kaydı");
        lblHeader.setStyle(
                "-fx-font-family: 'Montserrat Medium';" +
                        "-fx-text-fill: #e5e7eb;" +
                        "-fx-font-size: 18;" +
                        "-fx-font-weight: bold;"
        );

        // Müşteri Formu
        TextField cAd = createTextField();
        TextField cSoyad = createTextField();
        TextField cTel = createTextField();
        TextField cEmail = createTextField();

        GridPane musteriGrid = createFormGrid();

        int row = 0;
        addFormRow(musteriGrid, row++, "Ad", cAd);
        addFormRow(musteriGrid, row++, "Soyad", cSoyad);
        addFormRow(musteriGrid, row++, "Telefon (11 hane)", cTel);
        addFormRow(musteriGrid, row++, "E-posta", cEmail);

        VBox musteriCard = createCard("Müşteri Bilgileri", musteriGrid);

        // Araç Formu
        TextField cPlaka = createTextField();
        TextField cMarka = createTextField();
        TextField cModel = createTextField();
        TextField cUretimYili = createTextField();
        TextField cKilometre = createTextField();
        TextField cSonServis = createTextField();
        TextField cSasiNo = createTextField();
        TextField cYakitTipi = createTextField();

        GridPane aracGrid = createFormGrid();

        row = 0;
        addFormRow(aracGrid, row++, "Plaka", cPlaka);
        addFormRow(aracGrid, row++, "Marka", cMarka);
        addFormRow(aracGrid, row++, "Model", cModel);
        addFormRow(aracGrid, row++, "Üretim Yılı", cUretimYili);
        addFormRow(aracGrid, row++, "Kilometre", cKilometre);
        addFormRow(aracGrid, row++, "Son Servis Tarihi (gg.MM.yyyy)", cSonServis);
        addFormRow(aracGrid, row++, "Şasi No", cSasiNo);
        addFormRow(aracGrid, row++, "Yakıt Tipi", cYakitTipi);

        VBox aracCard = createCard("Araç Bilgileri", aracGrid);

        HBox centerForms = new HBox(20, musteriCard, aracCard);
        centerForms.setPadding(new Insets(16));
        centerForms.setAlignment(Pos.TOP_CENTER);

        Button btnKaydet = createColoredButton("Kaydı Tamamla", "#22c55e");
        Button btnGeri = createColoredButton("Giriş Ekranına Dön", "#6b7280");

        HBox buttonBar = new HBox(12, btnKaydet, btnGeri);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(12));

        VBox center = new VBox(10, lblHeader, centerForms, buttonBar);
        center.setAlignment(Pos.TOP_CENTER);
        center.setPadding(new Insets(16));
        center.setStyle("-fx-background-color: #020617;");

        BorderPane content = new BorderPane();
        content.setCenter(center);
        content.setStyle("-fx-background-color: #020617;");

        BorderPane root = createWindowChrome(stage, content, "VivaCar Oto Servis - Yeni Kayıt");

        Scene scene = new Scene(root, 980, 620);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

        btnGeri.setOnAction(e -> ctx.showLoginScreen(stage));

        btnKaydet.setOnAction(e -> {
            try {
                String ad = cAd.getText().trim();
                String soyad = cSoyad.getText().trim();
                String tel = cTel.getText().trim();
                String email = cEmail.getText().trim();

                String plakaRaw = cPlaka.getText().trim();
                String marka = cMarka.getText().trim();
                String model = cModel.getText().trim();
                String uretimYiliS = cUretimYili.getText().trim();
                String kmS = cKilometre.getText().trim();
                String sonServis = cSonServis.getText().trim();
                String sasiNo = cSasiNo.getText().trim();
                String yakitTipiRaw = cYakitTipi.getText().trim();

                if (ad.isEmpty() || soyad.isEmpty() || tel.isEmpty() || plakaRaw.isEmpty()) {
                    showAlert("Eksik Bilgi", "Ad, Soyad, Telefon ve Plaka boş olamaz.");
                    return;
                }

                if (!tel.matches("\\d{11}")) {
                    showAlert("Telefon Hatası", "Telefon numarası 11 haneli olmalıdır.\nÖrnek: 05523713980");
                    return;
                }

                Musteri mevcut = servis.musteriTelefonlaBul(tel);
                if (mevcut != null) {
                    showAlert("Telefon Zaten Kayıtlı",
                            "Bu telefon numarası ile zaten bir müşteri kaydı var.\n" +
                                    "Lütfen müşteri kodu ile giriş yapın.");
                    return;
                }

                if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    showAlert("E-posta Hatası", "Lütfen geçerli bir e-posta adresi giriniz.\nÖrnek: mertdoguc@hotmail.com");
                    return;
                }

                if (!sonServis.isEmpty() && !isGecerliServisTarihi(sonServis)) {
                    showAlert("Tarih Hatası", "Son servis tarihi gg.MM.yyyy formatında olmalı.");
                    return;
                }

                String plaka = normalizePlaka(plakaRaw);
                if (!isPlakaGecerli(plaka)) {
                    showAlert("Plaka Hatası", "Plaka formatı hatalı.\nÖrn: 35 CJG 255 veya 35 KM 2453");
                    return;
                }

                if (servis.aracAra(plaka) != null) {
                    showAlert("Plaka Zaten Kayıtlı", "Bu plaka ile sisteme kayıtlı bir araç zaten var.");
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

                int yeniMusteriID = ctx.yeniMusteriID();
                String yeniKod = servis.yeniMusteriKoduUret();

                Musteri musteri = new Musteri(yeniMusteriID, ad, soyad, tel, email, yeniKod);
                servis.musteriEkle(musteri);

                Arac arac = new Arac(plaka, marka, model, uretimYili, km, sonServis, sasiNo, yakitTipi);
                servis.musteriyeAracEkle(musteri, arac);

                showAlert("Kayıt Tamamlandı",
                        "Müşteri kaydınız oluşturuldu.\n\n" +
                                "Müşteri kodunuz: " + yeniKod +
                                "\nLütfen bu kodu kimseyle paylaşmayınız.");

                ctx.showLoginScreen(stage);

            } catch (NumberFormatException ex) {
                showAlert("Sayı Hatası", "Üretim yılı ve kilometre alanları sayısal olmalıdır.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Hata", "Kayıt oluşturulurken hata: " + ex.getMessage());
            }
        });
    }
}
